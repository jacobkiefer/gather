package com.example.gt.gather;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.FirebaseSimpleLoginError;
import com.firebase.simplelogin.FirebaseSimpleLoginUser;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends Activity implements FragmentA.Communicator, 
													  FragmentB.Communicator, 
													  FragmentCreate.Communicator, 
													  NewEventDialog.Communicator,
													  TimePickerFragment.Communicator,
													  DatePickerFragment.Communicator,
													  JoinEventDialog.Communicator,
													  LoginDialog.Communicator,
													  SignUpDialog.Communicator{
	
	FragmentManager manager;
	View createContainer;
	boolean isCreating;
	FragmentA fragA;
	FragmentB fragB;
	Firebase dataRef;
	SimpleLogin authClient;
	boolean isLoggedIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = getFragmentManager();
		dataRef = new Firebase("https://gatherapp.firebaseio.com/");
		authClient = new SimpleLogin(dataRef, getApplicationContext());
		
		if(savedInstanceState==null)
		{
				showLoginDialog();
		}
		else
		{
			onLogIn();
		}
	}
	
	@Override
	public void createUser(final String userName, final String password) {
		authClient.createUser(userName, password, new SimpleLoginAuthenticatedHandler() {
			
			@Override
			public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
				if(error != null)
				{
					Toast.makeText(getApplicationContext(), "Error. Could not create new user. Try again.", Toast.LENGTH_LONG).show();
				}
				else
				{
					verifyAndLogin(userName, password);
				}
			}
		});	
	}
	
	@Override
	public void verifyAndLogin(String userName, String password) {
		authClient.loginWithEmail(userName, password, new SimpleLoginAuthenticatedHandler() {
			
			@Override
			public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
				if (error != null)
				{
					Toast.makeText(getApplicationContext(), "Error. Could not login. Try again.", Toast.LENGTH_LONG).show();
					setIsLoggedIn(false);
				}
				else
				{
					LoginDialog loginDialog = (LoginDialog) manager.findFragmentByTag("Login Dialog");
					if (loginDialog != null) loginDialog.dismiss();
					SignUpDialog signUpDialog = (SignUpDialog) manager.findFragmentByTag("Sign Up Dialog");
					if (signUpDialog != null) signUpDialog.dismiss();
					setIsLoggedIn(true);
					onLogIn();
				}
			}
		});
	}
	
	public void setIsLoggedIn(boolean val){
		isLoggedIn = val;
	}
	
	public boolean getIsLoggedIn(){
		return isLoggedIn;
	}
	
	@Override
	public void showSignUpDialog() {
		SignUpDialog signUpDialog = new SignUpDialog();
		signUpDialog.show(manager, "Sign Up Dialog");
	}
	
	@Override
	public void onLogIn() {
		fragA = (FragmentA) manager.findFragmentById(R.id.fragment_a);
		fragB = (FragmentB) manager.findFragmentById(R.id.fragment_b);
		fragA.setCommunicator(this);
		fragB.setCommunicator(this);
		
		fragA.setDataBaseReference(dataRef);
		fragA.onLogIn();
		
		createContainer = findViewById(R.id.createContainer);
		if(isCreating)
		{
			createContainer.setVisibility(View.VISIBLE);
		}
		else
		{
			createContainer.setVisibility(View.GONE);
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
	        	if (createEventFrag == null)
	        	{
	        		isCreating = true;
	        		createEventFrag = new FragmentCreate();
	        		FragmentTransaction transaction = manager.beginTransaction();
	    			transaction.add(R.id.createContainer, createEventFrag, "CreateEventFragment");
	    			transaction.commit();
	    			createContainer.setVisibility(View.VISIBLE);
	    			fragB.setIsCreating(true);
	        	}
	        	else
	        	{
	        		cancelNewEvent();
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
	
	public void moveCameraToLocation(Event event, int i) {
		fragB.moveCameraToLocation(event, i);
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
		fragB.searchUpdateMarker(latlon);
	}

	@Override
	public void addMarker(Event event) {
		fragB.addMarker(event);		
	}

	@Override
	public void cancelNewEvent() {
		FragmentCreate createEventFrag = (FragmentCreate) manager.findFragmentByTag("CreateEventFragment");
    	FragmentTransaction transaction = manager.beginTransaction();
		isCreating = false;
		transaction.remove(createEventFrag);
		transaction.commit();
		createContainer.setVisibility(View.GONE);
		fragB.setIsCreating(false);
		fragB.cancelNewEvent();
		
	}

	@Override
	public void createNewEvent() {
		FragmentCreate createEventFrag = (FragmentCreate) manager.findFragmentByTag("CreateEventFragment");
    	FragmentTransaction transaction = manager.beginTransaction();
		transaction.remove(createEventFrag);
		transaction.commit();
		createContainer.setVisibility(View.GONE);
		NewEventDialog newEventDialog = new NewEventDialog();
		newEventDialog.show(manager, "newEventDialog");
	}

	@Override
	public void onBroadcastMessage(String name, String date, int numPeople) {
		Event newEvent = new Event(date, fragB.getNewEventLatitude(), fragB.getNewEventLongitude(), name, numPeople);
		fragB.setIsCreating(false);
		fragB.clearNewEvent();
		dataRef.child("events").push().setValue(newEvent);
		
	}
	
	@Override
	public void join(String id, int newNumPeople) {
		dataRef.child("events").child(id).child("numberOfPeople").setValue(newNumPeople);
		
	}

	@Override
	public void clearMap() {
		fragB.clearMap();		
	}

	@Override
	public void setDate(int year, int month, int day) {
		NewEventDialog newEventDialog = (NewEventDialog) manager.findFragmentByTag("newEventDialog");
    	newEventDialog.setDate(year, month, day);
	}

	@Override
	public void setTime(int hour, int min) {
		NewEventDialog newEventDialog = (NewEventDialog) manager.findFragmentByTag("newEventDialog");
    	newEventDialog.setTime(hour, min);
	}
	
	public void showLoginDialog(){
		LoginDialog loginDialog = new LoginDialog();
		loginDialog.show(manager, "Login Dialog");
	}

}
