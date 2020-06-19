package com.example.ibrataxi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserForgetPasswordActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the forget password page on the server
	private static final String FORGET_PASSWORD_URL = "http://ibrataxi.comze.com/user_forget_password.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	int errors;
	EditText email_txt;
	String email; // the user email
	String returned_password; // save the returned password

	String EMAIL_REGEX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_forget_password);

		errors = 0;

		EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		// define the button
		Button send_btn = (Button) findViewById(R.id.user_f_get_password_btn);

		// define the text fields ( email and password )
		email_txt = (EditText) findViewById(R.id.user_f_email_txt);

		send_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// validate the email
				if (email_txt.getText().toString().length() == 0
						|| !Pattern.matches(EMAIL_REGEX, email_txt.getText()
								.toString())) {
					errors += 1;
					email_txt.setError("Enter Valid Email");
				}

				// check the value of the errors variable
				// if it equal to 0; then goto the next activity
				// else; show error messages
				if (errors > 0) {
					// show error message
					Toast.makeText(UserForgetPasswordActivity.this,
							"Please Fix the error(s)", Toast.LENGTH_LONG)
							.show();

					// set the errors variable to 0
					errors = 0;
				} else {

					new AttemptForgetPassword().execute();

					email_txt.setText(""); // empty the email text
				}
			}
		});
	}

	// AsyncTask is a seperate thread than the thread that runs the GUI
	// Any type of networking should be done with asynctask.
	class AttemptForgetPassword extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(UserForgetPasswordActivity.this);
			pDialog.setMessage("Getting your password please wait ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;

			try {
				email = email_txt.getText().toString();

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", email));

				// getting user details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(
						FORGET_PASSWORD_URL, "POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					// Log.d("All Catalog information : ", json.toString());

					returned_password = json.getString(TAG_PASSWORD);

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

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			// show the returned password
			Toast.makeText(UserForgetPasswordActivity.this,
					"Your Password is : " + returned_password,
					Toast.LENGTH_LONG).show();

			// start the login activity
			Intent user_login = new Intent(UserForgetPasswordActivity.this,
					UserLoginActivity.class);
			startActivity(user_login);

		}
	}
}
