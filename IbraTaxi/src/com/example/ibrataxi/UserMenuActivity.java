package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserMenuActivity extends Activity {

	Button profile_btn;
	Button request_btn;
	Button logout_btn;

	TextView user_name_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_menu);

		profile_btn = (Button) findViewById(R.id.user_profile_btn);
		request_btn = (Button) findViewById(R.id.user_request_taxi_btn);
		logout_btn = (Button) findViewById(R.id.user_logout);

		user_name_txt = (TextView) findViewById(R.id.user_name_txt);

		// make a controller object to get user information in it
		Controller aController = (Controller) getApplicationContext();
		user_name_txt.setText("Welcome " + aController.getFirstName() + " "
				+ aController.getLastName());

		profile_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent profile = new Intent(UserMenuActivity.this,
						UserProfileActivity.class);
				startActivity(profile);
			}
		});

		request_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent request = new Intent(UserMenuActivity.this,
						UserRequestTaxiActivity.class);
				startActivity(request);
			}
		});

		logout_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent user_logout = new Intent(UserMenuActivity.this,
						TypeActivity.class);
				startActivity(user_logout);
			}
		});
	}
}
