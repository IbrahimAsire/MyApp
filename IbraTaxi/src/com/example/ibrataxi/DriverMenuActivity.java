package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DriverMenuActivity extends Activity {

	Button driver_change_status_btn;
	Button driver_profile_btn;
	Button driver_set_location_btn;
	Button driver_logout;
	Button driver_show_requests_btn;

	TextView driver_name_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_menu);

		driver_change_status_btn = (Button) findViewById(R.id.driver_change_status_btn);
		driver_profile_btn = (Button) findViewById(R.id.driver_profile_btn);
		driver_show_requests_btn = (Button) findViewById(R.id.driver_show_requests_btn);
		driver_set_location_btn = (Button) findViewById(R.id.driver_set_location_btn);
		driver_logout = (Button) findViewById(R.id.driver_logout_btn);

		driver_name_txt = (TextView) findViewById(R.id.driver_name_txt);

		// make a controller object to get user information in it
		Controller aController = (Controller) getApplicationContext();
		driver_name_txt.setText("Welcome " + aController.getFirstName() + " "
				+ aController.getLastName());

		driver_show_requests_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent driver_show_requests = new Intent(
						DriverMenuActivity.this,
						DriverAllRequestsActivity.class);
				startActivity(driver_show_requests);
			}
		});

		driver_change_status_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent driver_change_status = new Intent(
						DriverMenuActivity.this,
						DriverChangeStatusActivity.class);
				startActivity(driver_change_status);
			}
		});

		driver_set_location_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent driver_set_location = new Intent(
						DriverMenuActivity.this,
						DriverSetLocationActivity.class);
				startActivity(driver_set_location);
			}
		});

		driver_profile_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent driver_profile = new Intent(DriverMenuActivity.this,
						DriverProfileActivity.class);
				startActivity(driver_profile);
			}
		});

		driver_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent driver_logout = new Intent(DriverMenuActivity.this,
						TypeActivity.class);
				startActivity(driver_logout);
			}
		});
	}

}
