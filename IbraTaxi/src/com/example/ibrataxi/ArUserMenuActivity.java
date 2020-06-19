package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ArUserMenuActivity extends Activity {

	Button profile_btn;
	Button request_btn;
	Button logout_btn;

	TextView user_name_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_user_menu);

		profile_btn = (Button) findViewById(R.id.ar_user_profile_btn);
		request_btn = (Button) findViewById(R.id.ar_user_request_taxi_btn);
		logout_btn = (Button) findViewById(R.id.ar_user_logout);

		user_name_txt = (TextView) findViewById(R.id.ar_user_name_txt);

		// make a controller object to get user information in it
		Controller aController = (Controller) getApplicationContext();
		user_name_txt.setText("„—Õ»« »ﬂ Ì«  " + aController.getFirstName() + " "
				+ aController.getLastName());

		profile_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_profile = new Intent(ArUserMenuActivity.this,
						ArUserProfileActivity.class);
				startActivity(ar_profile);
			}
		});

		request_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_request = new Intent(ArUserMenuActivity.this,
						ArUserRequestTaxiActivity.class);
				startActivity(ar_request);
			}
		});

		logout_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_user_logout = new Intent(ArUserMenuActivity.this,
						ArTypeActivity.class);
				startActivity(ar_user_logout);
			}
		});
	}
}
