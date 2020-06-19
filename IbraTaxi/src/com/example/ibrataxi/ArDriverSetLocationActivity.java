package com.example.ibrataxi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

public class ArDriverSetLocationActivity extends FragmentActivity implements
		LocationListener, OnMarkerDragListener {

	// Google Map
	private GoogleMap googleMap;

	TextView currentlocation;

	// make a controller object to store location in it
	Controller aController;

	Button driver_change_location_btn;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// the url for the catalog page on the server
	private static final String CHANGE_LOCATION_URL = "http://ibrataxi.comze.com/driver_update_location.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	String location_long;
	String location_lati;
	String driver_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_activity_driver_set_location);

		// set the current location text view
		currentlocation = (TextView) findViewById(R.id.ar_current_location);

		driver_change_location_btn = (Button) findViewById(R.id.ar_driver_change_location_btn);

		// set the aController object
		aController = (Controller) getApplicationContext();

		// get the saved driver email
		driver_email = aController.getEmail();

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

			// save the current location in the controller
			aController.setLatitude(String.valueOf(location.getLatitude()));
			aController.setLongitude(String.valueOf(location.getLongitude()));

			// Adding a marker with the current location
			googleMap.addMarker(new MarkerOptions()
					.position(
							new LatLng(location.getLatitude(), location
									.getLongitude()))
					.title("«‰  Â‰«")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
					.draggable(true));

			// Setting latitude and longitude in the TextView tv_location
			currentlocation.setText("Latitude:" + location.getLatitude()
					+ "\n Longitude:" + location.getLongitude());

			// if (location != null) {
			// onLocationChanged(location);
			// }

			locationManager.requestLocationUpdates(provider, 2000, 0, this);

			// ///

		} catch (Exception e) {
			e.printStackTrace();
		}

		driver_change_location_btn
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						// call update status class
						new AttemptChange().execute();
					}
				});

	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		LatLng dragPosition = arg0.getPosition();
		String dragLat = String.valueOf(dragPosition.latitude);
		String dragLong = String.valueOf(dragPosition.longitude);

		// Setting latitude and longitude in the TextView tv_location
		currentlocation.setText("«·⁄—÷:" + dragLat + "\n «·ÿÊ·:" + dragLong);

		// save the changed user location in the controller
		aController.setLatitude(dragLat);
		aController.setLongitude(dragLong);

		Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);

		Toast.makeText(getApplicationContext(), " „  €ÌÌ— «·„Êﬁ⁄..!",
				Toast.LENGTH_SHORT).show();
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

		// // Setting latitude and longitude in the TextView tv_location
		// currentlocation.setText("Latitude:" + latitude + ", Longitude:"
		// + longitude);
		//
		// Log.e("Location is : ", latitude + ", " + longitude);
	}

	/**
	 * function to load map If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.ar_map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
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

	class AttemptChange extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ArDriverSetLocationActivity.this);
			pDialog.setMessage("„Õ«Ê·…  ÕœÌÀ «·„Êﬁ⁄ «·Õ«·Ì ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			// Check for success tag
			int success;

			try {

				// get the saved lati and long from the aController object
				location_lati = aController.getLatitude();
				location_long = aController.getLongitude();

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("location_lati",
						location_lati));
				params.add(new BasicNameValuePair("location_long",
						location_long));

				params.add(new BasicNameValuePair("driver_email", driver_email));

				// getting user details by making HTTP request
				JSONObject json = JSONParser.makeHttpRequest(
						CHANGE_LOCATION_URL, "POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

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

			// show successful updating message
			Toast.makeText(ArDriverSetLocationActivity.this, message,
					Toast.LENGTH_LONG).show();

			// go to the login activity
			Intent ar_driver_menu = new Intent(
					ArDriverSetLocationActivity.this,
					ArDriverMenuActivity.class);
			startActivity(ar_driver_menu);
		}
	}

}
