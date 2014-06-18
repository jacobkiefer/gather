package com.example.gt.gather;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentB extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	
	String[] eventTitles, eventTimes, eventSlots, eventDistances, eventPlayers;
	double[][] locations = {{33.776795,-84.403817}, {33.775600,-84.404096}, {33.774530,-84.395770}, {33.787442,-84.373754}, {33.773843,-84.398838}, {33.751455,-84.385041}};
	
	ArrayList<Marker> eventMarkers = new ArrayList<Marker>();
	final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1000;
	private LocationClient myLocationClient;
	private LatLng latlon;
	private Location location;
	private GoogleMap googleMap;

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
		Resources res = getResources();
		eventTitles = res.getStringArray(R.array.eventTitles);
		eventTimes = res.getStringArray(R.array.eventTimes);
		eventPlayers = res.getStringArray(R.array.eventPlayers);
		eventSlots = res.getStringArray(R.array.eventSlots);
		eventDistances = res.getStringArray(R.array.eventDistances);
		
		try {
			if (googleMap == null) {
				googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			}
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		} catch (Exception e){
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
		location = myLocationClient.getLastLocation();
		latlon = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon,17));
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(getActivity(), "Location Client Disconnected. Please re-connect (try restarting).",
				Toast.LENGTH_SHORT).show();
	}
	
	
	public void addMarker(int i) {
		LatLng markerLatLon = new LatLng(locations[i][0], locations[i][1]);
		Marker mark = googleMap.addMarker(new MarkerOptions().position(markerLatLon).title(eventTitles[i] + " ( " + eventDistances[i] +" mi.)").snippet(""+eventSlots[i]+" Slots Available, " + eventTimes[i]));
		if(mark!=null)	eventMarkers.add(mark);
	}

	public void moveCameraToLocation(int i) {
		LatLng markerLatLon = new LatLng(locations[i][0], locations[i][1]);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLatLon,17));
		eventMarkers.get(i).showInfoWindow();	
	}
    

}
