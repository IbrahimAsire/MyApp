package com.example.ibrataxi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ArUserRequestTaxiActivity extends FragmentActivity implements
		LocationListener, OnMarkerDragListener, OnMarkerClickListener {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	// url to get all locations list
	private static String URL_TAXI_LOCATIONS = "http://ibrataxi.comze.com/show_all_taxi_locations.php";
	private static String URL_DRIVER_INFO = "http://ibrataxi.comze.com/driver_info.php";
	private static String URL_USER_MAKE_REQUEST = "http://ibrataxi.comze.com/user_make_request.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	private static final String TAG_LOCATIONS = "locations";
	private static final String TAG_NAME = "driver_name";
	private static final String TAG_EMAIL = "driver_email";
	private static final String TAG_MOBILE = "driver_mobile";
	private static final String TAG_STATUS = "driver_status";
	private static final String TAG_LOCATION_LONG = "location_long";
	private static final String TAG_LOCATION_LATI = "location_lati";

	// locations JSONArray
	JSONArray locations = null;

	String email;
	String user_email;
	String user_mobile;
	double location_long;
	double location_lati;

	String driver_email, driver_name, driver_status, driver_mobile;

	// Google Map
	private GoogleMap googleMap;

	TextView driver_request_name, driver_request_mobile, driver_request_status,
			driver_request_email;
	Button user_make_request_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_user_request_taxi);

		// make a controller object to get the driver information from it
		Controller aController = (Controller) getApplicationContext();

		// set the aController object
		aController = (Controller) getApplicationContext();

		driver_request_name = (TextView) findViewById(R.id.ar_driver_request_name);
		driver_request_mobile = (TextView) findViewById(R.id.ar_driver_request_mobile);
		driver_request_status = (TextView) findViewById(R.id.ar_driver_request_status);
		driver_request_email = (TextView) findViewById(R.id.ar_driver_request_email);

		user_make_request_btn = (Button) findViewById(R.id.ar_user_make_request_btn);

		// get the user email to make request
		user_email = aController.getEmail();
		user_mobile = aController.getMobile();

		try {
			// Loading map
			initilizeMap();

			// Changing map type
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

			// Showing / hiding your current location
			googleMap.setMyLocationEnabled(true);

			// Enable / Disable zooming controls
			googleMap.getUiSettings().setZoomControlsEnabled(true);

			// Enable / Disable my location button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);

			// Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);

			// Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);

			googleMap.setOnMarkerDragListener(this);

			// ///

			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location
			Location location = locationManager.getLastKnownLocation(provider);

			// Adding a marker with the current location for the user
			// googleMap.addMarker(new MarkerOptions()
			// .position(
			// new LatLng(location.getLatitude(), location
			// .getLongitude()))
			// .title("Your Are Here")
			// .snippet("Population: 4,137,400")
			// .icon(BitmapDescriptorFactory
			// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

			// if (location != null) {
			// onLocationChanged(location);
			// }

			locationManager.requestLocationUpdates(provider, 2000, 0, this);

			// get the taxi location from the server to set the marker
			new LoadAllTaxiLocations().execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// button to call the driver
		user_make_request_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// call the make request class
				new AttempMakeRequest().execute();

				if (driver_request_status.getText().toString() != "Busy") {
					// open the call activity to call the driver
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"
							+ driver_request_mobile.getText().toString()));
					startActivity(callIntent);

				} else {
					Toast.makeText(ArUserRequestTaxiActivity.this,
							"„‰ ›÷·ﬂ ﬁ„ »›Õ’ Õ«·… «·”«∆ﬁ", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	/**
	 * Background Async Task to Load all locations by making HTTP Request
	 * */
	class LoadAllTaxiLocations extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArUserRequestTaxiActivity.this);
			pDialog.setMessage("«‰ Ÿ— Õ Ï Ì „  Õ„Ì· Ã„Ì⁄ «·”«∆ﬁÌ‰ ⁄·Ï «·Œ—Ìÿ…...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("test", "test"));

			// getting JSON string from URL
			JSONObject json = JSONParser.makeHttpRequest(URL_TAXI_LOCATIONS,
					"POST", params);

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// locations found
					// Getting Array of locations
					locations = json.getJSONArray(TAG_LOCATIONS);

					// Log.d("Locations : ", json.toString());

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
			// dismiss the dialog after getting all products
			pDialog.dismiss();

			try {
				// looping through All locations
				for (int i = 0; i < locations.length(); i++) {
					JSONObject c = locations.getJSONObject(i);

					// Storing each json item in variable
					email = c.getString(TAG_EMAIL);
					location_long = c.getDouble(TAG_LOCATION_LONG);
					location_lati = c.getDouble(TAG_LOCATION_LATI);

					// System.out.println("Location  : " + location_long);

					// Log.println();

					// Adding a marker with the current location for the
					// user
					LatLng pos = new LatLng(location_lati, location_long);

					googleMap
							.addMarker(new MarkerOptions()
									.position(pos)
									.title(email)
									.snippet(c.getString(TAG_STATUS))
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Background Async Task to Load all locations by making HTTP Request
	 * */
	class AttempMakeRequest extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArUserRequestTaxiActivity.this);
			pDialog.setMessage("«‰ Ÿ— Õ Ï Ì „ Õ›Ÿ ÿ·»ﬂ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("driver_email", email));
			params.add(new BasicNameValuePair("user_email", user_email));
			params.add(new BasicNameValuePair("user_mobile", user_mobile));

			Log.d("data : ", params.toString());

			// getting JSON string from URL
			JSONObject json = JSONParser.makeHttpRequest(URL_USER_MAKE_REQUEST,
					"POST", params);

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				return json.getString(TAG_MESSAGE);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String message) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();

			// show successful updating message
			Toast.makeText(ArUserRequestTaxiActivity.this, message,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// Log.i("GoogleMapActivity", "onMarkerClick");

		// visible the call button
		user_make_request_btn.setVisibility(View.VISIBLE);

		email = marker.getTitle();

		driver_request_email.setText(email);
		driver_request_status.setText(marker.getSnippet());

		// get the driver information
		new AttemptDriverInfo().execute();

		// Toast.makeText(getApplicationContext(),
		// "Marker Clicked: " + marker.getTitle(), Toast.LENGTH_LONG)
		// .show();

		return false;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

	}

	/**
	 * function to load map If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.ar_user_map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}

			googleMap.setOnMarkerClickListener(this);
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	// AsyncTask is a seperate thread than the thread that runs the GUI
	// Any type of networking should be done with asynctask.
	class AttemptDriverInfo extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArUserRequestTaxiActivity.this);
			pDialog.setMessage("«·Õ’Ê· ⁄·Ï »Ì«‰«  «·”«∆ﬁ «·„Õœœ ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("driver_email", email));

				// getting user details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(URL_DRIVER_INFO,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				Log.d("Information : ", json.toString());

				if (success == 1) {

					// save the driver information in the driver vars
					driver_name = json.getString("driver_name");
					driver_mobile = json.getString("driver_mobile");

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

			// set the email text with the value of the title
			driver_request_name.setText(driver_name);
			driver_request_mobile.setText(driver_mobile);
		}
	}
}
