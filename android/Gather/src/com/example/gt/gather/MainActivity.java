package com.example.gt.gather;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements FragmentA.Communicator{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FragmentA fragA = (FragmentA) getFragmentManager().findFragmentById(R.id.fragment_a);
		fragA.setCommunicator(this);
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
	        	Intent intent = new Intent(this, CreateActivity.class);
				startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void moveCameraToLocation(int i) {
		FragmentManager manager = getFragmentManager();
		FragmentB fragB = (FragmentB) manager.findFragmentById(R.id.fragment_b);
		fragB.moveCameraToLocation(i);
	}
	
	public void populateMap(int i){
		FragmentManager manager = getFragmentManager();
		FragmentB fragB = (FragmentB) manager.findFragmentById(R.id.fragment_b);
		fragB.addMarker(i);
	}

}
