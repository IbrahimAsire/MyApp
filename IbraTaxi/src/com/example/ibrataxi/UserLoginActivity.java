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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserLoginActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the login page on the server
	private static final String LOGIN_URL = "http://ibrataxi.comze.com/user_login.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	// input fields
	EditText email_txt;
	EditText password_txt;

	int errors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);

		errors = 0;

		// define the buttons
		Button login_btn = (Button) findViewById(R.id.user_login_btn);
		Button register_btn = (Button) findViewById(R.id.user_register_btn);
		Button forget_btn = (Button) findViewById(R.id.user_forget_btn);

		// setup the text fields ( email and password )
		email_txt = (EditText) findViewById(R.id.user_email_txt);
		password_txt = (EditText) findViewById(R.id.user_password_txt);

		login_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// validate the email and the password
				if (email_txt.getText().toString().length() == 0) {
					errors += 1;
					email_txt.setError("Email Required");
				} else if (password_txt.getText().toString().length() == 0) {
					errors += 1;
					password_txt.setError("Password Required");
				}

				// check the value of the errors variable
				// if it equal to 0; then goto the next activity
				// else; show error messages

				if (errors > 0) {
					// show error message
					Toast.makeText(UserLoginActivity.this,
							"Please Fix the error(s)", Toast.LENGTH_LONG)
							.show();

					// set the errors variable to 0
					errors = 0;
				} else {

					// call the attemptLogin class
					new AttemptLogin().execute();
				}
			}
		});

		forget_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent forget_password = new Intent(UserLoginActivity.this,
						UserForgetPasswordActivity.class);
				startActivity(forget_password);
			}
		});

		register_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent register = new Intent(UserLoginActivity.this,
						UserRegisterActivity.class);
				startActivity(register);
			}
		});
	}

	// AsyncTask is a seperate thread than the thread that runs the GUI
	// Any type of networking should be done with asynctask.
	class AttemptLogin extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UserLoginActivity.this);
			pDialog.setMessage("Attempting login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;
			String email = email_txt.getText().toString();
			String password = password_txt.getText().toString();

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("password", password));

				// getting user details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(LOGIN_URL, "POST",
						params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				Log.d("All User information : ", json.toString());
				
				if (success == 1) {

					 Log.d("All User information : ", json.toString());

					// make a controller object to store user information in it
					Controller aController = (Controller) getApplicationContext();

					// save the user information in the controller class
					aController.setId(json.getInt("id"));
					aController.setEmail(json.getString("email"));
					aController.setPasswprd(json.getString("password"));
					aController.setFirstName(json.getString("first_name"));
					aController.setLastName(json.getString("last_name"));
					aController.setAddress(json.getString("address"));
					aController.setMobile(json.getString("mobile"));

					// goto the user menu activity
					Intent user_menu = new Intent(UserLoginActivity.this,
							UserMenuActivity.class);
					startActivity(user_menu);

					// type = json.getString("type");

					return json.getString(TAG_MESSAGE);

				} else {
					return json.getString(TAG_MESSAGE);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/

		protected void onPostExecute(String message) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			// show successful updating message
			Toast.makeText(UserLoginActivity.this, message, Toast.LENGTH_LONG)
					.show();

		}
	}
}