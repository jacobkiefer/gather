package com.example.gt.gather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.FirebaseSimpleLoginError;
import com.firebase.simplelogin.FirebaseSimpleLoginUser;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements FragmentA.Communicator, 
													  FragmentB.Communicator, 
													  FragmentCreate.Communicator, 
													  NewEventDialog.Communicator,
													  TimePickerFragment.Communicator,
													  DatePickerFragment.Communicator,
													  JoinEventDialog.Communicator,
													  com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, 
													  com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener{
	
	FragmentManager manager;
	View createContainer;
	boolean isCreating;
	FragmentA fragA;
	FragmentB fragB;
	Firebase dataRef;
	SimpleLogin authClient;
	User myUser;
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient googleApiClient;
	boolean intentInProgress;
	String signedInWith;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = getFragmentManager();
		fragA = (FragmentA) manager.findFragmentById(R.id.fragment_a);
		fragB = (FragmentB) manager.findFragmentById(R.id.fragment_b);
		fragA.setCommunicator(this);
		fragB.setCommunicator(this);
		dataRef = new Firebase("https://gatherapp.firebaseio.com/");
		authClient = new SimpleLogin(dataRef, getApplicationContext());
		createContainer = findViewById(R.id.createContainer);
		if(isCreating)
		{
			createContainer.setVisibility(View.VISIBLE);
		}
		else
		{
			createContainer.setVisibility(View.GONE);
		}
		checkLoginStatus();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100)
		{
			String msg = data.getStringExtra("LoginType");
			switch (msg) 
			{
			case "Google":
				loginWithGoogle();
				signedInWith = "Google";
				break;
			case "Facebook":
				loginWithFacebook();
			case "Twitter":
				loginWithTwitter();
			default:
				break;
			}
//				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				myUser = new User("User", 5280.0/2);
		}
		else if (requestCode == RC_SIGN_IN) {
			intentInProgress = false;
			if (!googleApiClient.isConnecting()){
				googleApiClient.connect();
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
	        case R.id.act_logout: 
	        	{
	        		if(googleApiClient.isConnected())
	        		{
	        			Plus.AccountApi.clearDefaultAccount(googleApiClient);
	        			googleApiClient.disconnect();
	        		}
//	        		authClient.logout();
	        		checkLoginStatus();
	        	}
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isCreating", isCreating);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (googleApiClient != null)
		{
			if(googleApiClient.isConnected())
			{
				googleApiClient.disconnect();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		if( isFinishing ())	authClient.logout();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void moveCameraToLocation(Event event, int i) {
		fragB.moveCameraToLocation(event, i);
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
	public void onBroadcastMessage(String name, String date, int capacity) {
			Event newEvent = new Event(name, date, fragB.getNewEventLatitude(), fragB.getNewEventLongitude(), capacity, myUser.getEmail());
			fragB.setIsCreating(false);
			fragB.clearNewEvent();
			dataRef.child("events").push().setValue(newEvent);
	}
	
	@Override
	public void join(String id) {
		dataRef.child("events").child(id).child("users").push().setValue(myUser.getEmail());	
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

	public double getUserLatitude(){
		return myUser.getLat();
	}
	public double getUserLongitude(){
		return myUser.getLng();
	}

	@Override
	public void findAddress(LatLng latlng, int whoCalled) {
		FragmentCreate fragCreate = (FragmentCreate) manager.findFragmentByTag("CreateEventFragment");
		if(fragCreate!=null)
		{
			new ReverseGeocodingTask(this,fragCreate.addressSearchBox).execute(latlng);
		}
	}

	@Override
	public void updateLocation(Location loc) {
		if(myUser != null) 
		{	double[] latlng = {loc.getLatitude(), loc.getLongitude()};
			myUser.setLocation(latlng);
			fragA.refresh(myUser);
			fragB.setCircle(myUser.getRadius());
		}
		
	}
	
	public void checkLoginStatus(){
		authClient.checkAuthStatus(new SimpleLoginAuthenticatedHandler() {
			  public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
				  String myMsg = "";
				  if (error != null) 
				  {
						String msg = error.getMessage();
						switch (error.getCode()) 
						{
						case AccessNotGranted:
							myMsg = "Access Not Granted.";
							break;
						default:
							myMsg = "Error Checking Log-In Status. Please Try Again";
							break;						
						}
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
						Toast.makeText(getApplicationContext(), myMsg, Toast.LENGTH_LONG).show();
				}
			    else if (user == null) 
			    {
			    	Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			    	startActivityForResult(loginIntent, 100);
//			    	Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT).show();
			    } 
			    else 
			    {
			    	Log.d("JMK", "User Logged IN");
			        // There is a logged in user
			    	myUser = new User("User", 5280.0/2);
			    }
			  }
			});
	}
	
	
	
	public void loginWithFacebook(){
		
	}
	
	public void loginWithTwitter(){
		
	}
	
	public void loginWithGoogle(){
		googleApiClient = new GoogleApiClient.Builder(this)
									.addConnectionCallbacks(this)
									.addOnConnectionFailedListener(this)
									.addApi(Plus.API)
									.addScope(Plus.SCOPE_PLUS_PROFILE)
									.build();
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if(!intentInProgress && result.hasResolution())
		{
			try
			{
				intentInProgress = true;
				result.startResolutionForResult(this, RC_SIGN_IN);
			}
			catch (SendIntentException e)
			{
				intentInProgress = false;
				googleApiClient.connect();
			}
		}
		
	}
	

	@Override
	public void onConnected(Bundle arg0) {
//		fragA.setDataBaseReference(dataRef);
//		fragB.onLogin();
//		 Google+ Sign In Connection Success
		Toast.makeText(this, "User is connected to Google!", Toast.LENGTH_LONG).show();
		String scope = "oauth2:" + Scopes.PLUS_LOGIN;
		
		AsyncTask task = new AsyncTask() {
			String accessToken = null;
			@Override
			protected Object doInBackground(Object... params) {
				String scope = "oauth2:" + Scopes.PLUS_LOGIN;
				try {
					// We can retrieve the token to check via
					// token info or to pass to a service-side
					// application.
					accessToken = GoogleAuthUtil.getToken(getApplicationContext(),
						      Plus.AccountApi.getAccountName(googleApiClient),
						      scope);
//					Log.d("JMK", "Token "+accessToken);
					
				} catch (UserRecoverableAuthException e) {
					// This error is recoverable, so we could fix this
					// by displaying the intent to the user.
					accessToken = null;
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}catch (Exception e) {
					  throw new RuntimeException(e);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result) {
//				Log.d("JMK", "Token "+accessToken);
			  authClient.loginWithGoogle(accessToken, new SimpleLoginAuthenticatedHandler() {
			  
		      public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
			    if (error != null) {
			     Log.d("JMK","error with loginwithgoogle");
//			     Toast.makeText(getApplicationContext(), "error with loginwithgoogle", Toast.LENGTH_SHORT).show();
			    }
			    else {
			      // Logged in with Google+
//			    	Toast.makeText(getApplicationContext(), "User is connected to Firebase!", Toast.LENGTH_LONG).show();
			    	Log.d("JMK", "User is connected to Firebase");
			    	fragA.setDataBaseReference(dataRef);
					fragB.onLogin();
			    	
			    }
			  }
			});
				super.onPostExecute(result);
			}
		};
	task.execute((Void) null);
	}
//		try {
//			Toast.makeText(getApplicationContext(), "trying token " + accessToken, Toast.LENGTH_SHORT).show();
//		  accessToken = GoogleAuthUtil.getTokenWithNotification(this,
//		      Plus.AccountApi.getAccountName(googleApiClient),
//		      scope, new Bundle());
//		  Toast.makeText(getApplicationContext(), "got token " + accessToken, Toast.LENGTH_SHORT).show();
//		  authClient.loginWithGoogle(accessToken, new SimpleLoginAuthenticatedHandler() {
//		  
//	      public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
//		    if (error != null) {
//		     Toast.makeText(getApplicationContext(), "error with loginwithgoogle", Toast.LENGTH_SHORT).show();
//		    }
//		    else {
//		      // Logged in with Google+
//		    	Toast.makeText(getApplicationContext(), "User is connected to Firebase!", Toast.LENGTH_LONG).show();
//		    	fragA.setDataBaseReference(dataRef);
//				fragB.onLogin();
//		    	
//		    }
//		  }
//		});
//		  
//		} catch (IOException transientEx) {
//		  // network or server error, the call is expected to succeed if you try again later.
//		  // Don't attempt to call again immediately - the request is likely to
//		  // fail, you'll hit quotas or back-off.
//		  return;
//		} catch (UserRecoverableAuthException e) {
//		  // Recover
//		  accessToken = null;
//		} catch (GoogleAuthException authEx) {
//		  // Failure. The call is not expected to ever succeed so it should not be
//		  // retried.
//		  return;
//		} catch (Exception e) {
//		}		 
//	}


	@Override
	public void onConnectionSuspended(int arg0) {
		googleApiClient.connect();		
	}
}

//AsyncTask task = new AsyncTask() {
//	@Override
//	protected Object doInBackground(Object... params) {
//		String accessToken = null;
//		String scope = "oauth2:" + Plus.SCOPE_PLUS_PROFILE;
//		try {
//			// We can retrieve the token to check via
//			// token info or to pass to a service-side
//			// application.
//			accessToken = GoogleAuthUtil.getToken(getApplicationContext(),
//				      Plus.AccountApi.getAccountName(googleApiClient),
//				      scope);
//			
//		} catch (UserRecoverableAuthException e) {
//			// This error is recoverable, so we could fix this
//			// by displaying the intent to the user.
//			accessToken = null;
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (GoogleAuthException e) {
//			e.printStackTrace();
//		}catch (Exception e) {
//			  throw new RuntimeException(e);
//		}
//		return null;
//	}
//};
//task.execute((Void) null);
