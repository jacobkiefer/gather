package com.example.gt.gather;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
        
	private Context context;
	private View outputView;
	private int textOrEdit;
	
	public ReverseGeocodingTask(Context c, EditText out){
		context = c;
		outputView = out;
		textOrEdit = 2;
	}
	public ReverseGeocodingTask(Context c, TextView out){
		context = c;
		outputView = out;
		textOrEdit = 1;
	}
 
        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
        	Geocoder geocoder = new Geocoder(context, Locale.US);
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
                return "Sorrry, we could not find the postal address for this location";
            }
 
            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);
                
                addressText = String.format("%s, %s",
                		address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", 
                				address.getLocality());
            }
 
            return addressText;
        }
 
        @Override
        protected void onPostExecute(String addressText) {
            if(textOrEdit == 1) ((TextView) outputView).setText(addressText);
            if(textOrEdit == 2) ((EditText) outputView).setText(addressText);
        }
    }