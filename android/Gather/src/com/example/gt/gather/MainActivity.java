package com.example.gt.gather;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends Activity implements FragmentA.CommunicatorA, FragmentB.CommunicatorB, FragmentCreate.CommunicatorC{
	
	FragmentManager manager;
	View createContainer;
	boolean isCreating;
	FragmentA fragA;
	FragmentB fragB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = getFragmentManager();
		fragA = (FragmentA) manager.findFragmentById(R.id.fragment_a);
		fragB = (FragmentB) manager.findFragmentById(R.id.fragment_b);
		fragA.setCommunicator(this);
		fragB.setCommunicator(this);
		createContainer = findViewById(R.id.createContainer);
		createContainer.setVisibility(View.GONE);
		if (savedInstanceState != null)
		{
			isCreating = savedInstanceState.getBoolean("isCreating");
			if(isCreating)
			{
				createContainer.setVisibility(View.VISIBLE);
				createContainer.bringToFront();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	  } 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.act_create:
//	        	Intent intent = new Intent(this, CreateActivity.class);
//				startActivity(intent);
//	            return true;
	        	FragmentCreate createEventFrag = (FragmentCreate) manager.findFragmentByTag("CreateEventFragment");
	        	FragmentTransaction transaction = manager.beginTransaction();
	        	if (createEventFrag == null)
	        	{
	        		isCreating = true;
	        		createEventFrag = new FragmentCreate();
	        		createEventFrag.setCommunicator(this);
	    			transaction.add(R.id.createContainer, createEventFrag, "CreateEventFragment");
	    			transaction.commit();
	    			createContainer.setVisibility(View.VISIBLE);
	    			createContainer.bringToFront();
	    			fragB.setIsCreating(true);
	        	}
	        	else
	        	{
	        		isCreating = false;
	        		transaction.remove(createEventFrag);
	        		transaction.commit();
	        		createContainer.setVisibility(View.GONE);
	        		fragB.setIsCreating(false);
	        	}
	        	     	
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isCreating", isCreating);
		
	}
	
	public void moveCameraToLocation(int i) {
		fragB.moveCameraToLocation(i);
	}
	
	public void populateMap(int i){
		fragB.addMarker(i);
	}

	@Override
	public void passLocation(String text) {
//		Toast.makeText(this, ""+point[0]+" "+point[1], Toast.LENGTH_SHORT).show();
		FragmentCreate fragCreate = (FragmentCreate) manager.findFragmentByTag("CreateEventFragment");
		if(fragCreate!=null)
		{
			fragCreate.setAddressSearchBox(text);
		}
		
	}

	@Override
	public void updateNewEventMarker(double[] latlon) {
		fragB.searchUpdateToMarker(latlon);
	}

}
