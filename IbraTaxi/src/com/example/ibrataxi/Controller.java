package com.example.ibrataxi;

import android.app.Application;

public class Controller extends Application {

	// user information
	private int id;
	private String first_name;
	private String last_name;
	private String email;
	private String password;
	private String address;
	private String mobile;
	private String type;

	private String latitude;
	private String longitude;

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPasswprd(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}