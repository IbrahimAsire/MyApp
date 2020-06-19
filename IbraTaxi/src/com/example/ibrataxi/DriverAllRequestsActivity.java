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

public class DriverAllRequestsActivity extends Activity {

	String driver_email;

	// make a controller object to store user information in it
	Controller aController;

	Button driver_main_menu_btn;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the login page on the server
	private static final String TRACK_URL = "http://ibrataxi.comze.com/driver_track.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ORDERS = "orders";
	private static final String TAG_ORDER_USER_MOBILE = "order_user_mobile";
	private static final String TAG_ORDER_DATE = "order_date";

	ListView driver_order_lst;

	// define the list array
	ArrayList<String> values = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_all_requests);

		aController = (Controller) getApplicationContext();

		// set the email string with the saved email
		driver_email = aController.getEmail();

		driver_main_menu_btn = (Button) findViewById(R.id.driver_main_menu_btn);

		driver_main_menu_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// start the activity menu
				Intent menu = new Intent(DriverAllRequestsActivity.this,
						DriverMenuActivity.class);
				startActivity(menu);
			}
		});

		new AttemptOrder().execute();
	}

	class AttemptOrder extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DriverAllRequestsActivity.this);
			pDialog.setMessage("Attempting To Get All orders Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			// make a controller object to get user details from it
			Controller aController = (Controller) getApplicationContext();

			// Check for success tag
			int success;

			try {

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("driver_email", driver_email));

				// getting all orders details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(TRACK_URL, "POST",
						params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					Log.d("Track Info ", json.toString());

					// add all the orders from the database to the array list
					// orders JSONArray
					JSONArray orders = json.getJSONArray(TAG_ORDERS);

					for (int i = 0; i < orders.length(); i++) {
						JSONObject c = orders.getJSONObject(i);

						values.add(c.getString(TAG_ORDER_USER_MOBILE)
								+ "\t\t\t" + c.getString(TAG_ORDER_DATE));

						// ords += c.getInt(TAG_ORDER_NO) + "\n";
						// stats += c.getString(TAG_ORDER_STATUS) + "\n";
					}

					return json.getString(TAG_MESSAGE);

				} else {
					return json.getString(TAG_MESSAGE);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			// define order list
			driver_order_lst = (ListView) findViewById(R.id.driver_orders_lst);

			// define array adapter
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					DriverAllRequestsActivity.this,
					android.R.layout.simple_list_item_1, values);

			// assign the adapter to the order list view
			driver_order_lst.setAdapter(adapter);
		}
	}
}
