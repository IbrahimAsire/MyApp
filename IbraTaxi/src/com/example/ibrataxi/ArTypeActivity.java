package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ArTypeActivity extends Activity {

	Button driver_btn;
	Button user_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_type);

		driver_btn = (Button) findViewById(R.id.ar_driver_btn);
		user_btn = (Button) findViewById(R.id.ar_user_btn);

		driver_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_driver_login = new Intent(ArTypeActivity.this,
						ArDriverLoginActivity.class);
				startActivity(ar_driver_login);
			}
		});

		user_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent ar_user_login = new Intent(ArTypeActivity.this,
						ArUserLoginActivity.class);
				startActivity(ar_user_login);
			}
		});
	}

}
