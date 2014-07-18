package com.example.gt.gather;

import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.PI;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentB extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener,
		OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener{
	
	
	final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1000;
	
	private LocationClient myLocationClient;
	private Location location;
	private Circle myCircle;
	private double myRadius;
	private GoogleMap googleMap;
	private Marker newEventMarker;
	private ArrayList<Marker> eventMarkers = new ArrayList<Marker>();
	double[] cameraPosition;
	boolean isCreating;
	float cameraZoom;
	Communicator comm;
	LocationRequest myLocationRequest;
	boolean locationUpdatesRequested;
	SharedPreferences prefs;
	Editor prefEditor;
	boolean readyForUpdates;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLocationClient = new LocationClient(getActivity().getApplicationContext(),this,this);
		
		myLocationRequest = LocationRequest.create();
		myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		myLocationRequest.setInterval(10000);
		myLocationRequest.setFastestInterval(1000);
		myLocationRequest.setSmallestDisplacement((float) 20.0);
		locationUpdatesRequested = true;
		
		prefs = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		prefEditor = prefs.edit();
		prefEditor.putBoolean("KEY_UPDATES_ON", true);
		prefEditor.commit();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_b, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		double[] newEventLatLng = null;
		if(savedInstanceState != null)
		{
			cameraPosition = savedInstanceState.getDoubleArray("lastCameraPosition");
			cameraZoom = savedInstanceState.getFloat("lastCameraZoom");
			isCreating = savedInstanceState.getBoolean("isCreating");
			newEventLatLng = savedInstanceState.getDoubleArray("newEventLatLng");
		}
		
		try 
		{
			if (googleMap == null) 
			{
				googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			}
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			if (cameraPosition != null)
			{
				LatLng latlng = new LatLng(cameraPosition[0], cameraPosition[1]);
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,cameraZoom));
			}
			googleMap.setOnMapClickListener(this);
			googleMap.setOnMapLongClickListener(this);
			googleMap.setOnMarkerDragListener(this);
			
			if(newEventLatLng != null)
			{   
				addMarker(newEventLatLng);
			}
			
		} 
		catch (Exception e)
		{
			Toast.makeText(getActivity(), "We're having trouble getting the map. Please wait a moment and try again.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	public void onLogin(){
		if (checkPlayServices()) // good to go
		{
			myLocationClient.connect();
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		comm = (Communicator) getActivity();
		super.onAttach(activity);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		if (locationUpdatesRequested)
		{
			myLocationClient.requestLocationUpdates(myLocationRequest, this);
		}
	}
	
	@Override
	public void onPause() {
		prefEditor.putBoolean("KEY_UPDATES_ON", locationUpdatesRequested);
		prefEditor.commit();
		super.onPause();
	}
	
	@Override
	public void onStop() {
		if (myLocationClient.isConnected())
		{
			myLocationClient.removeLocationUpdates(this);
		}
		myLocationClient.disconnect();
		super.onStop();
	}
	
	@Override
	public void onResume() {
		if (prefs.contains("KEY_UPDATES_ON"))
		{
			locationUpdatesRequested = prefs.getBoolean("KEY_UPDATES_ON", false);
//			Toast.makeText(getActivity(), "updates: "+locationUpdatesRequested, Toast.LENGTH_SHORT).show();
		}
		else
		{
			prefEditor.putBoolean("KEY_UPDATES_ON", false);
			prefEditor.commit();
		}
		super.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		double[] camPos = {googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude};
		outState.putDoubleArray("lastCameraPosition", camPos );
		outState.putFloat("lastCameraZoom", googleMap.getCameraPosition().zoom);
		outState.putBoolean("isCreating", getIsCreating());
		
		if (newEventMarker!= null)
		{
			double[] newEventLatLng = {newEventMarker.getPosition().latitude, newEventMarker.getPosition().longitude};
			outState.putDoubleArray("newEventLatLng", newEventLatLng);
		}
	}

	private boolean checkPlayServices(){
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(status!= ConnectionResult.SUCCESS)
		{
			if(GooglePlayServicesUtil.isUserRecoverableError(status))
			{
				showErrorDialog(status);
			}
			else
			{
				Toast.makeText(getActivity(), "This device is not supported.", Toast.LENGTH_LONG).show();
			}
			return false;
		}		
		return true;
	}
	
	void showErrorDialog(int code) 
	{
		GooglePlayServicesUtil.getErrorDialog(code, getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  switch (requestCode) {
	    case CONNECTION_FAILURE_RESOLUTION_REQUEST:
	      if (resultCode == Activity.RESULT_CANCELED) 
	      {
	        Toast.makeText(getActivity(), "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
	      }
	      else if (resultCode == Activity.RESULT_OK)
	      {
	    	  // try request again
	    	  if(checkPlayServices()); // good to go
	      }
	      return;
	  }
	  super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(getActivity(),CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(getActivity(), "Sorry the location client was disconnected. " +
				"We will have to re-connect to get location services. Please try restarting the app",
				Toast.LENGTH_SHORT).show();
	}
	
//	public double[] getUserLocation(){
//		if (location == null && myLocationClient.isConnected())
//		{
//			location = myLocationClient.getLastLocation();
//		}
//		if (location != null)
//		{
//			double[] latlng = {location.getLatitude(), location.getLongitude()};
//			return latlng;
//		}
//		else
//		{
//			Toast.makeText(getActivity(), "We had trouble getting you're location. We'll assume you are on campus, until we can get a fix on your location.", Toast.LENGTH_LONG).show();
//			double[] latlng = {33.775618, -84.396285};	// Ga tech
//			return latlng;
//		}
//	}
	
	public void addMarker(Event event) {
		LatLng latlng = new LatLng(event.getCoords().getLatitude(), event.getCoords().getLongitude());
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latlng).title(event.getName()).snippet(""+event.getUsers().size()+" people attending"));
		eventMarkers.add(marker);
	}
	public void addMarker(double[] pointLatLng){
		LatLng latlng = new LatLng(pointLatLng[0], pointLatLng[1]);
		newEventMarker = googleMap.addMarker(new MarkerOptions().position(latlng).title("Create New Event"));
		newEventMarker.setDraggable(true);
		newEventMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		newEventMarker.showInfoWindow();
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17));
	}

	
	public void searchUpdateMarker(double[] pointLatLng){
		if (newEventMarker == null)
		{
			addMarker(pointLatLng);
		}
		else
		{
			LatLng latlng = new LatLng(pointLatLng[0], pointLatLng[1]);
			newEventMarker.setPosition(latlng);
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17));
		}
	}

	public void moveCameraToLocation(Event event, int i) {
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventMarkers.get(i).getPosition(),17));
		eventMarkers.get(i).showInfoWindow();
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
//		Toast.makeText(getActivity(), ""+newEventMarker.getPosition().latitude+ " =?= "+marker.getPosition().latitude, Toast.LENGTH_LONG).show();
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),17));
		comm.findAddress(marker.getPosition(), 1);
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapClick(LatLng pointLatLng) {
		if(getIsCreating())
		{
			if(newEventMarker == null)
			{
				double[] latlng = {pointLatLng.latitude, pointLatLng.longitude};
				addMarker(latlng);
			}
			else
			{
				newEventMarker.setPosition(pointLatLng);
//				newEventMarker.showInfoWindow();
			}
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointLatLng,17));
			comm.findAddress(pointLatLng, 1);
		}	
	}
	
	public double getNewEventLatitude(){
		return newEventMarker.getPosition().latitude;
	}
	
	public double getNewEventLongitude(){
		return newEventMarker.getPosition().longitude;
	}
	
	public void cancelNewEvent(){
		if(newEventMarker != null)
		{
			newEventMarker.remove();
			newEventMarker = null;
		}	
	}
	
	public void clearNewEvent(){
		newEventMarker.remove();
		newEventMarker = null;
	}
	
	public void clearMap(){
		for (Marker marker: eventMarkers) {
	        marker.remove();
	    }
		eventMarkers.clear();
	}
	
	public void setCircle(double rad){
		myRadius = rad*0.3048;  // m
		if (location != null && myCircle == null)
		{
		
			myCircle = googleMap.addCircle(new CircleOptions()
			.center(new LatLng(location.getLatitude(), location.getLongitude()))
			.radius(myRadius)
			.fillColor(0x403366CC)
			.strokeColor(Color.TRANSPARENT));
			
			if (cameraPosition == null)
			{	
				updateCameraForCircle(myCircle);				
			}
		}
		else if(location!=null && myCircle != null)
		{
			myCircle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
		}
	}
	
	public void updateCameraForCircle(Circle circle){
		
//		Log.d("JMK", "Center: "+circle.getCenter().latitude+" "+myCircle.getCenter().longitude);
		LatLng p1 = getCirclePoint(circle.getCenter(), myRadius/1000, 1);
		LatLng p2 = getCirclePoint(circle.getCenter(), myRadius/1000, 2);
		LatLng p3 = getCirclePoint(circle.getCenter(), myRadius/1000, 3);
		LatLng p4 = getCirclePoint(circle.getCenter(), myRadius/1000, 4);
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(p1);
		builder.include(p2);
		builder.include(p3);
		builder.include(p4);
		
		LatLngBounds bounds = builder.build();				
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 4));
		
	}
	
	
	
	public LatLng getCirclePoint(LatLng center, double radius, int i){
		double lat1 = center.latitude*PI/180.0;
		double lng1 = center.longitude*PI/180.0;
		double bearing;
		double earthRad = 3963.1676;
		double d = radius/earthRad;
//		Log.d("JMK", "radius "+radius);
		switch (i) 
		{
			case 1:
				bearing = 90.0;
				break;
			case 2:
				bearing = 180.0;
				break;
			case 3:
				bearing = 270.0;
				break;
			default:
				bearing = 0.0;
				break;
		}
		bearing *= PI/180;
		double lat2 = asin(sin(lat1)*cos(d)+cos(lat1)*sin(d)*cos(bearing));
		double lng2 = lng1 + atan2((sin(bearing)*sin(d)*cos(lat1)), (cos(d)-sin(lat1)*sin(lat2)));
//		Log.d("JMK", "lat: "+lat2*180/PI+" lng: "+lng2*180/PI);
		return new LatLng(lat2*180/PI, lng2*180/PI);
	}
	
	public void setIsCreating(boolean val){
		isCreating = val;
	}
	public boolean getIsCreating(){
		return isCreating;
		
	}
	
	public void setCommunicator(Communicator c)
	{
		comm = c;
	}
	
	interface Communicator
	{
		public void findAddress(LatLng latlng, int itWasB);
		public void updateLocation(Location location);
//		public void onConnectSetUserLocation(double[] latlng);
	}

	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		comm.updateLocation(loc);
//		Toast.makeText(getActivity(), "location changed", Toast.LENGTH_SHORT).show();
	}
	
}
