package com.example.gt.gather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CreateActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		
		Intent intent = getIntent();
		int index = intent.getIntExtra("index", 0);
		
//		FragmentB fb = (FragmentB) getFragmentManager().findFragmentById(R.id.fragment_b);
//		if(fb!=null);
		
	}

}
