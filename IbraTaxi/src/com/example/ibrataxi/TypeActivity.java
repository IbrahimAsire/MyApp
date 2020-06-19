package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class TypeActivity extends Activity {

	Button driver_btn;
	Button user_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type);

		driver_btn = (Button) findViewById(R.id.driver_btn);
		user_btn = (Button) findViewById(R.id.user_btn);

		driver_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent driver_login = new Intent(TypeActivity.this,
						DriverLoginActivity.class);
				startActivity(driver_login);
			}
		});

		user_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent user_login = new Intent(TypeActivity.this,
						UserLoginActivity.class);
				startActivity(user_login);
			}
		});
	}

}
