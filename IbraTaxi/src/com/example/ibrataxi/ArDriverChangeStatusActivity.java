package com.example.ibrataxi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ArDriverChangeStatusActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the catalog page on the server
	private static final String CHANGE_STATUS_URL = "http://ibrataxi.comze.com/driver_update_status.php";
	private static final String GET_STATUS_URL = "http://ibrataxi.comze.com/driver_get_current_status.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	Spinner driver_status_spinner;
	Button driver_change_btn;
	TextView driver_current_status_txt;
	
	String driver_email;
	String driver_status;
	String current_status;

	// define the list array
	ArrayList<String> status_values = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_driver_change_status);
		
		driver_current_status_txt = (TextView) findViewById(R.id.ar_driver_current_status_txt);
		
		// set the spinner to the activity
		driver_status_spinner = (Spinner) findViewById(R.id.ar_driver_status_spinner);

		// make a controller object to get the driver information from it
		Controller aController = (Controller) getApplicationContext();

		// get the saved driver email
		driver_email = aController.getEmail();

		// add the status values to the spinner
		status_values.add("Available");
		status_values.add("Busy");
		status_values.add("Offline");

		// call get status class
		new AttemptGetStatus().execute();
		
		// define array adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ArDriverChangeStatusActivity.this,
				android.R.layout.simple_spinner_item, status_values);

		// assign the adapter to the status spinner
		driver_status_spinner.setAdapter(adapter);

		driver_change_btn = (Button) findViewById(R.id.ar_driver_change_btn);

		driver_change_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// get the status value from the spinner
				driver_status = (String) driver_status_spinner
						.getSelectedItem();

				// call update status class
				new AttemptUpdate().execute();
			}
		});
	}

	class AttemptGetStatus extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArDriverChangeStatusActivity.this);
			pDialog.setMessage("Ã«—Ì «·Õ’Ê· ⁄·Ï «·Õ«·… «·Õ«·Ì…  ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			// Check for success tag
			int success;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("driver_email", driver_email));

				// getting user details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(GET_STATUS_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				
				// set the current_Status with the returned value from the server
				current_status = json.getString("driver_status");

				return json.getString(TAG_MESSAGE);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String message) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			// show successful updating message
			Toast.makeText(ArDriverChangeStatusActivity.this, message,
					Toast.LENGTH_LONG).show();

			// set the status textView with the returned status from the server
			driver_current_status_txt.setText(current_status);
		}
	}
	
	class AttemptUpdate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArDriverChangeStatusActivity.this);
			pDialog.setMessage("Ì „  ÕœÌÀ «·Õ«·… «·Œ«’… »ﬂ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			// Check for success tag
			int success;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("driver_status",
						driver_status));
				params.add(new BasicNameValuePair("driver_email", driver_email));

				// getting user details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(CHANGE_STATUS_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				return json.getString(TAG_MESSAGE);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String message) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			// show successful updating message
			Toast.makeText(ArDriverChangeStatusActivity.this, message,
					Toast.LENGTH_LONG).show();

			// go to the login activity
			Intent ar_driver_menu = new Intent(ArDriverChangeStatusActivity.this,
					ArDriverMenuActivity.class);
			startActivity(ar_driver_menu);

		}
	}

}
