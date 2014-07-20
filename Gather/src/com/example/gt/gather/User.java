package com.example.gt.gather;

import java.util.ArrayList;

import com.firebase.simplelogin.FirebaseSimpleLoginUser;

import android.location.Location;

public class User {
	
//	String email;
//	String firstName;
//	String lastName;
	ArrayList<String> tags;
	ArrayList<Event> events;
	FirebaseSimpleLoginUser firebaseUser;
	
	double[] latlng;
	String location;
	int radius;
	
	public User(String email, int radius){
//		setEmail(email);
		setRadius(radius);
	}
	
//	public void setEmail(String email){
//		this.email = email;
//	}
//	public void setName(String first, String last){
//		firstName = first;
//		lastName = last;
//	}
	
	public void setFirebaseSimpleLoginUser(FirebaseSimpleLoginUser user){
		firebaseUser = user;
	}
	public void setLocation(double[] latlng){
		if (latlng != null)
		{
			this.latlng = latlng;
		}
	}
	public void setLocation(Location loc){
		if (loc != null)
		{
			latlng[0] = loc.getLatitude();
			latlng[1] = loc.getLongitude();
		}
	}
	public void setRadius(int rad){
		radius = rad;
	}
	public void addTag(String tag){
		tags.add(tag);
	}
	public void removeTag(String tag){
		tags.remove(tag);
	}
	public void addEvent(Event event){
		events.add(event);
	}
	public void removeEvent(Event event){
		events.remove(event);
	}
	public FirebaseSimpleLoginUser getFirebaseSimpleLoginUser(){
		return firebaseUser;
	}
	
//	public String getEmail(){
//		return email;
//	}
//	public String getFirstName(){
//		return firstName;
//	}
//	public String getLastName(){
//		return lastName;
//	}
	public double getLat(){
		return latlng[0];
	}
	public double getLng(){
		return latlng[1];
	}
	public double[] getCoords(){
		return latlng;
	}
	public int getRadius(){
		return radius;
	}
	public String getTag(int pos){
		return tags.get(pos);
	}
	public Event getEvent(int pos){
		return events.get(pos);
	}
	
	

}
