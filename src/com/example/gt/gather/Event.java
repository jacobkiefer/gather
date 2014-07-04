package com.example.gt.gather;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Event {
	
	private String name;
    private String date;
    private int numberOfPeople;
    private double latitude;
    private double longitude;
    
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Event() { }

    public Event(String date, double latitude, double longitude, String name, int numberOfPeople) {
        this.name = name;
        this.date = date;
        this.numberOfPeople = numberOfPeople;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
    
    public int getNumberOfPeople() {
    	return numberOfPeople;
    }
    
    public double getLatitude() {
    	return latitude;
    }
    
    public double getLongitude() {
    	return longitude;
    }
    
    public void setLocation(LatLng latlng)
    {
    	latitude = latlng.latitude;
    	longitude = latlng.longitude;
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    public void setDate(String date){
    	this.date = date;
    }
    
    public void setNumPeople(int num){
    	numberOfPeople = num;
    }
}

