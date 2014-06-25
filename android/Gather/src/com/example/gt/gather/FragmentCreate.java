package com.example.gt.gather;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCreate extends DialogFragment {
	
	Button cancelButton, createButton;
	EditText addressSearchBox;
	double[] searchResultLatLon;
	CommunicatorC comm;
	
	@Override
	public void onAttach(Activity activity) {
//		Log.d("JMK", "Fragment Create Event onAttach");
		super.onAttach(activity);	
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		Log.d("JMK", "Fragment Create Event onCreate");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create,  container, false);
		cancelButton = (Button) view.findViewById(R.id.cancelButton);
		createButton = (Button) view.findViewById(R.id.createButton);
		addressSearchBox = (EditText) view.findViewById(R.id.addressSearchBox);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		createButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addressSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			   if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
			        return false;
			    } else if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			        
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.hideSoftInputFromWindow(addressSearchBox.getWindowToken(), 0);
	                Log.d("JMK", "Search Clicked");
	                new AdressSearchClicked(addressSearchBox.getText().toString()).execute();
	                return true;
				}
				return false;
			}
		});
		
//		Log.d("JMK", "Fragment Create Event onCreateView");
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("JMK", "Fragment Create Event onActivityCreated");
		if (savedInstanceState != null)
		{
			addressSearchBox.setText(savedInstanceState.getString("addressSearchBoxText"));
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}
	
	private class AdressSearchClicked extends AsyncTask<Void, Void, Boolean> {
		private String toSearch;
		private Address address;
		
		public AdressSearchClicked(String toSearch){
			this.toSearch = toSearch;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			try
			{
				Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
				List<Address> results = geocoder.getFromLocationName(toSearch, 1);
				
				if (results.size() == 0)
				{
					return false;
				}
				else
				{
					address = results.get(0);
					double[] latlonResult = {address.getLatitude(), address.getLongitude()};
					searchResultLatLon = latlonResult;
				}
			}
			catch (Exception e)
			{
				Log.e("JMK", "Something went wrong with getFromLocationName", e);
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result)
			{
//				Toast.makeText(getActivity(), "toSearch - Lat: " +searchResultLatLon[0]+" Lon: "+searchResultLatLon[1], Toast.LENGTH_LONG).show();
				String addressText = String.format("%s, %s, %s",
                		address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", 
                				address.getLocality(), 
                					address.getCountryName());
				addressSearchBox.setText(addressText);
				comm.updateNewEventMarker(searchResultLatLon);
			}
			else
			{
				Toast.makeText(getActivity(), "Couldn't find the location", Toast.LENGTH_LONG).show();
			}
			
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		Log.d("JMK", "Fragment Create Event onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
//		Log.d("JMK", "Fragment Create Event onStop");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroy();
//		Log.d("JMK", "Fragment Create Event onDestroyView");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		Log.d("JMK", "Fragment Create Event onDestroy");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
//		Log.d("JMK", "Fragment Create Event onDetach");
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("addressSearchBoxText", addressSearchBox.getText().toString());
	}
	
	public void setAddressSearchBox(String text)
	{
		addressSearchBox.setText(text);
	}
	
	public void setCommunicator(CommunicatorC c)
	{
		comm = c;
	}
	
	interface CommunicatorC {
		public void updateNewEventMarker(double[] latlon);
	}


}

