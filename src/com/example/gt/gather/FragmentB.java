package com.example.gt.gather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import android.app.Activity;
//import android.app.Dialog;
//import android.app.DialogFragment;
import android.app.Fragment;
//import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentB extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener{
	
	
	final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1000;
	
	private LocationClient myLocationClient;
	private Location location;
	private GoogleMap googleMap;
	private Marker newEventMarker;
	private ArrayList<Marker> eventMarkers = new ArrayList<Marker>();
	double[] cameraPosition;
	boolean isCreating;
	float cameraZoom;
	Communicator comm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLocationClient = new LocationClient(getActivity().getApplicationContext(),this,this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_b, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null)
		{
			cameraPosition = savedInstanceState.getDoubleArray("lastCameraPosition");
			cameraZoom = savedInstanceState.getFloat("lastCameraZoom");
			isCreating = savedInstanceState.getBoolean("isCreating");
		}
		
		try {
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
			
			if (savedInstanceState != null)
			{
				double[] newEventLatLng = savedInstanceState.getDoubleArray("newEventLatLng");
				if(newEventLatLng != null)
				{   
					addMarker(newEventLatLng);
				}
			}
		} 
		catch (Exception e)
		{
			Toast.makeText(getActivity(), "Failed to get map", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	@Override
	public void onStart() {
		if (checkPlayServices()) // good to go
		{
		myLocationClient.connect();
		}
		super.onStart();
	}
	
	@Override
	public void onStop() {
		myLocationClient.disconnect();
		super.onStop();
	}
	
	@Override
	public void onResume() {
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
	public void onConnected(Bundle arg0) {
		if (cameraPosition == null)
		{
			location = myLocationClient.getLastLocation();
			LatLng latlng;
			if (location!=null)
			{
				latlng = new LatLng(location.getLatitude(), location.getLongitude());
			}
			else
			{
				latlng = new LatLng(33.775618, -84.396285); // Ga Tech
			}
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,17));
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(getActivity(), "Location Client Disconnected. Please re-connect (try restarting).",
				Toast.LENGTH_SHORT).show();
	}
	
	
	public void addMarker(Event event) {
		LatLng latlng = new LatLng(event.getLatitude(), event.getLongitude());
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latlng).title(event.getName()).snippet(""+event.getNumberOfPeople()+" people attending"));
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
		new ReverseGeocodingTask().execute(marker.getPosition());
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
			new ReverseGeocodingTask().execute(pointLatLng);
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
			double[] latlng = null;
			if(newEventMarker != null)
			{ 
				double[] temp = {newEventMarker.getPosition().latitude, newEventMarker.getPosition().longitude};
				latlng = temp;
			}
			googleMap.clear();
			eventMarkers.clear();
			if(getIsCreating() && latlng != null)
			{
				addMarker(latlng);
			}
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
		public void passLocation(String text);
	}
	
	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
         
 
        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
        	Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;
            
            List<Address> addresses = null;
            String addressText="";
 
            try 
            {
            	addresses = geocoder.getFromLocation(latitude, longitude,1);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
 
            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);
                
                addressText = String.format("%s, %s, %s",
                		address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", 
                				address.getLocality(), 
                					address.getCountryName());
            }
 
            return addressText;
        }
 
        @Override
        protected void onPostExecute(String addressText) {
            comm.passLocation(addressText);
        }
    }
}
