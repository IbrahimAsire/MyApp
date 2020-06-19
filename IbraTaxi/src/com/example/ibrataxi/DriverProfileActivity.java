package com.example.ibrataxi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DriverProfileActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the login page on the server
	private static final String UPDATE_URL = "http://ibrataxi.comze.com/driver_update_profile.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	int errors;

	EditText password, first_name, last_name, address, mobile, email_txt;
	Button update_btn;

	// for mobile patterns
	String MOBILE_REGEX;
	String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_profile);

		errors = 0;

		// mobile pattern
		MOBILE_REGEX = "[0-9]{10}";

		// make a controller object to store user information in it
		Controller aController = (Controller) getApplicationContext();

		// set the email string with the saved email
		email = aController.getEmail();

		password = (EditText) findViewById(R.id.driver_p_password_txt);
		first_name = (EditText) findViewById(R.id.driver_p_first_name_txt);
		last_name = (EditText) findViewById(R.id.driver_p_last_name_txt);
		address = (EditText) findViewById(R.id.driver_p_address_txt);
		mobile = (EditText) findViewById(R.id.driver_p_mobile_txt);
		email_txt = (EditText) findViewById(R.id.driver_p_email_txt);

		// make the email not editable
//		email_txt.setFocusable(false);

		// get the user information from the controller class
		password.setText(aController.getPassword());
		first_name.setText(aController.getFirstName());
		last_name.setText(aController.getLastName());
		address.setText(aController.getAddress());
		mobile.setText(aController.getMobile());
		email_txt.setText(aController.getEmail());

		update_btn = (Button) findViewById(R.id.driver_p_update_btn);

		update_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// validate the first name
				if (first_name.getText().toString().length() == 0) {
					errors += 1;
					first_name.setError("Enter Valid First Name");
				}

				// validate the last name
				else if (last_name.getText().toString().length() == 0) {
					errors += 1;
					last_name.setError("Enter Valid Last Name");
				}

				// validate the password ( not less than six character )
				else if (password.getText().toString().length() == 0
						|| password.getText().toString().length() < 6) {
					errors += 1;
					password.setError("Enter Valid Password (not less than six characters");
				}

				// validate the address
				else if (address.getText().toString().length() == 0) {
					errors += 1;
					address.setError("Enter Valid Address");
				}

				// validate the mobile number
				else if (mobile.getText().toString().length() == 0
						|| mobile.getText().toString().length() > 10
						|| !Pattern.matches(MOBILE_REGEX, mobile.getText()
								.toString())) {
					errors += 1;
					mobile.setError("Enter Valid Mobile number (ex: 0501234567)");
				}

				// check the value of the errors variable
				// if it equal to 0; then goto the next activity
				// else; show error messages
				if (errors > 0) {
					// show error message
					Toast.makeText(DriverProfileActivity.this,
							"Please Fix the error(s)", Toast.LENGTH_LONG)
							.show();

					// set the errors variable to 0
					errors = 0;
				} else {

					// call the attemptLogin class
					new AttemptUpdate().execute();
				}

			}
		});
	}

	class AttemptUpdate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DriverProfileActivity.this);
			pDialog.setMessage("Attempting Updating Information...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			// Check for success tag
			int success;

			String f_name = first_name.getText().toString();
			String l_name = last_name.getText().toString();
			String pass = password.getText().toString();
			String add = address.getText().toString();
			String mob = mobile.getText().toString();
			String new_email = email_txt.getText().toString();

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("first_name", f_name));
				params.add(new BasicNameValuePair("last_name", l_name));
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("new_email", new_email));
				params.add(new BasicNameValuePair("password", pass));
				params.add(new BasicNameValuePair("address", add));
				params.add(new BasicNameValuePair("mobile", mob));

				// getting driver details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(UPDATE_URL,
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
			Toast.makeText(DriverProfileActivity.this, message,
					Toast.LENGTH_LONG).show();

			// go to the login activity
			Intent login = new Intent(DriverProfileActivity.this,
					DriverLoginActivity.class);
			startActivity(login);

		}
	}
}
