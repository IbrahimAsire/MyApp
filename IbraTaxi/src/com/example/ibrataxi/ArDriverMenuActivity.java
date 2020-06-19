package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ArDriverMenuActivity extends Activity {

	Button driver_change_status_btn;
	Button driver_profile_btn;
	Button driver_set_location_btn;
	Button driver_logout;
	Button driver_show_requests_btn;

	TextView driver_name_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_driver_menu);

		driver_change_status_btn = (Button) findViewById(R.id.ar_driver_change_status_btn);
		driver_profile_btn = (Button) findViewById(R.id.ar_driver_profile_btn);
		driver_show_requests_btn = (Button) findViewById(R.id.ar_driver_show_requests_btn);
		driver_set_location_btn = (Button) findViewById(R.id.ar_driver_set_location_btn);
		driver_logout = (Button) findViewById(R.id.ar_driver_logout_btn);

		driver_name_txt = (TextView) findViewById(R.id.ar_driver_name_txt);

		// make a controller object to get user information in it
		Controller aController = (Controller) getApplicationContext();
		driver_name_txt.setText("„—Õ»« »ﬂ Ì«  " + aController.getFirstName() + " "
				+ aController.getLastName());

		driver_show_requests_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_driver_show_requests = new Intent(
						ArDriverMenuActivity.this,
						ArDriverAllRequestsActivity.class);
				startActivity(ar_driver_show_requests);
			}
		});

		driver_change_status_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_driver_change_status = new Intent(
						ArDriverMenuActivity.this,
						ArDriverChangeStatusActivity.class);
				startActivity(ar_driver_change_status);
			}
		});

		driver_set_location_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_driver_set_location = new Intent(
						ArDriverMenuActivity.this,
						ArDriverSetLocationActivity.class);
				startActivity(ar_driver_set_location);
			}
		});

		driver_profile_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_driver_profile = new Intent(ArDriverMenuActivity.this,
						ArDriverProfileActivity.class);
				startActivity(ar_driver_profile);
			}
		});

		driver_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_driver_logout = new Intent(ArDriverMenuActivity.this,
						ArTypeActivity.class);
				startActivity(ar_driver_logout);
			}
		});
	}

}
