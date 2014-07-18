package com.example.gt.gather;

import java.util.ArrayList;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.PI;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;


public class Event {
	
	private String name;
    private String date;
    private int capacity;
    private Coords coords;
    private ArrayList<String> users;
    
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Event() { }

    public Event(String name, String date, double latitude, double longitude, int cap, String userName) {
        this.name = name;
        this.date = date;
        this.capacity = cap;
        users = new ArrayList<String>();
        users.add(userName);
        coords = new Coords(latitude, longitude);
    }
    
    public class Coords {
    	public double latitude, longitude;
    	// Required default constructor for Firebase object mapping
        @SuppressWarnings("unused")
        public Coords() { }
    	public Coords(double latitude, double longitude){
    		this.latitude = latitude;
    		this.longitude = longitude;
    	}
    	public double getLatitude() {
    		return latitude;
    	}
    	public void setLatitude(double lat) {
    		latitude = lat;
    	}
    	public double getLongitude() {
    		return longitude;
    	}
    	public void setLongitude(double lng){
    		longitude = lng;
    	}
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
    
    public int getCapacity(){
    	return capacity;
    }
    public ArrayList<String> getUsers() {
    	return users;
    }
    
    public Coords getCoords() {
    	return coords;
    }   
    
    public double findDistance(double myLat, double myLng){
//    	Log.d("JMK", "my position: "+myLat+" "+myLng);
//    	Log.d("JMK", "event position: "+latitude+" "+longitude);
    	double d2r = PI/180.0;
    	double earthRad = 3963.1676; // miles
    	double dlat = (getCoords().getLatitude() - myLat)*d2r;
//    	Log.d("JMK", "dlat: "+dlat);
    	double dlng = (getCoords().getLongitude() - myLng)*d2r;
//    	Log.d("JMK", "dlon: "+dlng);
    	double a = (sin(dlat/2)*sin(dlat/2)) + cos(myLat*d2r)*cos(getCoords().getLatitude()*d2r)*(sin(dlng/2)*sin(dlng/2));
//    	Log.d("JMK", "a: "+a);
    	double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//    	Log.d("JMK", "c: "+c);
    	double d = earthRad*c;
//    	Log.d("JMK", "d: "+d);
//    	if (d > 0.75)
//    	{
//    		return d;
//    	}
//    	else
//    	{
    		return d*5280; // ft
//    	}
    }
    
    public void setLocation(LatLng latlng)
    {
    	coords.setLatitude(latlng.latitude);
    	coords.setLongitude(latlng.longitude);
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    public void setDate(String date){
    	this.date = date;
    }
    
    public void addUser(String user){
    	users.add(user);
    }
    
    public void setCapacity(int cap){
    	capacity = cap;
    }
}

