package com.example.ibrataxi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class DriverShowRequestsActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the login page on the server
	private static final String REQUEST_URL = "http://ibrataxi.comze.com/driver_show_requests.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	Button driver_main_menu_btn;

	ListView request_lst;

	// define the list array
	ArrayList<String> values = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_show_requests);

		driver_main_menu_btn = (Button) findViewById(R.id.driver_main_menu_btn);

		driver_main_menu_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent driver_menu = new Intent(
						DriverShowRequestsActivity.this,
						DriverMenuActivity.class);
				startActivity(driver_menu);
			}
		});

		// call the get request class to get all the requested to the driver
		new AttemptGetRequests().execute();
	}

	class AttemptGetRequests extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DriverShowRequestsActivity.this);
			pDialog.setMessage("Attempting To Get your Requests Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			// make a controller object to get driver details from it
			Controller aController = (Controller) getApplicationContext();

			// Check for success tag
			int success;

			// save the order details
			String driver_email = aController.getEmail();

			try {

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("driver_email", driver_email));

				// getting driver details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(REQUEST_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				String reqsts = "";
				String stats = "";

				Log.d("Request Info ", json.toString());
				
				if (success == 1) {

					Log.d("Request Info ", json.toString());

					// add all the requests from the database to the array list
					// requests JSONArray
					JSONArray requests = json.getJSONArray("requests");

					for (int i = 0; i < requests.length(); i++) {
						JSONObject c = requests.getJSONObject(i);

						values.add(c.getString("request_mobile") + "\t\t\t"
								+ c.getString("request_date"));

						// reqsts += c.getInt(TAG_ORDER_NO) + "\n";
						// stats += c.getString(TAG_ORDER_STATUS) + "\n";
					}

					// Log.d("requests_no" , reqsts);
					// Log.d("Status" , stats);

				} else {
					return json.getString(TAG_MESSAGE);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String message) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			// define order list
//			request_lst = (ListView) findViewById(R.id.driver_requests_lst);

			// define array adapter
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					DriverShowRequestsActivity.this,
					android.R.layout.simple_list_item_1, values);

			// assign the adapter to the order list view
			request_lst.setAdapter(adapter);
		}
	}

}
