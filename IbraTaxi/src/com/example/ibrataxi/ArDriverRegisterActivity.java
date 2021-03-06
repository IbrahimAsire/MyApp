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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ArDriverRegisterActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the login page on the server
	private static final String Register_URL = "http://ibrataxi.comze.com/driver_register.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	int errors;

	EditText fname_txt;
	EditText lname_txt;
	EditText email_txt;
	EditText address_txt;
	EditText password_txt;
	EditText mobile_txt;

	Button register_btn;

	// for email and mobile patterns
	String EMAIL_REGEX;
	String MOBILE_REGEX;
	String PASSWORD_REGEX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_driver_register);

		errors = 0;

		// email pattern
		EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		// mobile pattern
		MOBILE_REGEX = "[0-9]{10}";

		// password pattern
		PASSWORD_REGEX = "^(?=.*\\d).{8,15}$"; // from 8 to 15 length and must
												// contain at least one number

		fname_txt = (EditText) findViewById(R.id.ar_driver_r_fname_txt);
		lname_txt = (EditText) findViewById(R.id.ar_driver_r_lname_txt);
		email_txt = (EditText) findViewById(R.id.ar_driver_r_email_txt);
		address_txt = (EditText) findViewById(R.id.ar_driver_r_address_txt);
		password_txt = (EditText) findViewById(R.id.ar_driver_r_password_txt);
		mobile_txt = (EditText) findViewById(R.id.ar_driver_r_mobile_txt);

		register_btn = (Button) findViewById(R.id.ar_driver_r_register_btn);

		register_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// validate the email
				if (email_txt.getText().toString().length() == 0
						|| !Pattern.matches(EMAIL_REGEX, email_txt.getText()
								.toString())) {
					errors += 1;
					email_txt.setError("���� ����� ����");
				}

				// validate the first name
				else if (fname_txt.getText().toString().length() == 0) {
					errors += 1;
					fname_txt.setError("��� ����");
				}

				// validate the last name
				else if (lname_txt.getText().toString().length() == 0) {
					errors += 1;
					lname_txt.setError("��� ����");
				}

				// validate the password ( not less than six character )
				else if (password_txt.getText().toString().length() == 0
						|| password_txt.getText().toString().length() < 8
						|| !Pattern.matches(PASSWORD_REGEX, password_txt
								.getText().toString())) {
					errors += 1;
					password_txt
							.setError("���� ���� ���� ����� �� 8 ��� 15 ��� ���� �� ����� ��� ��� ���� ��� �����");
				}

				// validate the address
				else if (address_txt.getText().toString().length() == 0) {
					errors += 1;
					address_txt.setError("��� ����");
				}

				// validate the mobile number
				else if (mobile_txt.getText().toString().length() == 0
						|| mobile_txt.getText().toString().length() > 10
						|| !Pattern.matches(MOBILE_REGEX, mobile_txt.getText()
								.toString())) {
					errors += 1;
					mobile_txt.setError("���� ��� ���� (ex: 0501234567)");
				}

				// check the value of the errors variable
				// if it equal to 0; then goto the next activity
				// else; show error messages
				if (errors > 0) {
					// show error message
					Toast.makeText(ArDriverRegisterActivity.this,
							"�� ������ ������� ����", Toast.LENGTH_LONG)
							.show();

					// set the errors variable to 0
					errors = 0;
				} else {

					// call the attemptLogin class
					new AttemptRegister().execute();
				}
			}
		});
	}

	class AttemptRegister extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArDriverRegisterActivity.this);
			pDialog.setMessage("����� ���� ����...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			// Check for success tag
			int success;

			String first_name = fname_txt.getText().toString();
			String last_name = lname_txt.getText().toString();
			String email = email_txt.getText().toString();
			String password = password_txt.getText().toString();
			String address = address_txt.getText().toString();
			String mobile = mobile_txt.getText().toString();

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("first_name", first_name));
				params.add(new BasicNameValuePair("last_name", last_name));
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("password", password));
				params.add(new BasicNameValuePair("address", address));
				params.add(new BasicNameValuePair("mobile", mobile));

				// getting driver details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(Register_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				// return the message from the server
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

			// show the returned message from the server
			Toast.makeText(ArDriverRegisterActivity.this, message,
					Toast.LENGTH_LONG).show();

			// go to the login activity
			Intent ar_driver_login = new Intent(ArDriverRegisterActivity.this,
					ArDriverLoginActivity.class);
			startActivity(ar_driver_login);
		}
	}
}
