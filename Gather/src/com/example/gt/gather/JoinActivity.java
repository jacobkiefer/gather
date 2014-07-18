package com.example.gt.gather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class JoinActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		
		Intent intent = getIntent();
		
		FragmentB fb = (FragmentB) getFragmentManager().findFragmentById(R.id.fragment_b);
		if(fb!=null);
		
	}

}
