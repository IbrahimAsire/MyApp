package com.example.ibrataxi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageView help_img = (ImageView) findViewById(R.id.help_img);

		Button english_btn = (Button) findViewById(R.id.english_btn);

		english_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent english_login = new Intent(MainActivity.this,
						TypeActivity.class);
				startActivity(english_login);
			}
		});

		Button arabic_btn = (Button) findViewById(R.id.araic_btn);

		arabic_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent arabic_login = new Intent(MainActivity.this,
						ArTypeActivity.class);
				startActivity(arabic_login);
			}
		});

		help_img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent help = new Intent(MainActivity.this, HelpActivity.class);
				startActivity(help);
			}
		});
	}
}
