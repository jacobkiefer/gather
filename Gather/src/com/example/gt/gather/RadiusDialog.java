package com.example.gt.gather;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class RadiusDialog extends DialogFragment implements OnSeekBarChangeListener, OnClickListener {
	
	Communicator comm;
	SeekBar radiusBar;
	TextView radiusText;
	Button cancelRadiusButton;
	Button updateRadiusButton;
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		comm = (Communicator) activity;
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("Adjust Listening Radius");
		View view = inflater.inflate(R.layout.radius_dialog, null);
		radiusBar = (SeekBar) view.findViewById(R.id.radiusSeekBar);
		radiusText = (TextView) view.findViewById(R.id.radiusText);
		cancelRadiusButton = (Button) view.findViewById(R.id.cancelRadiusButton);
		updateRadiusButton = (Button) view.findViewById(R.id.updateRadiusButton);
		
		radiusBar.setOnSeekBarChangeListener(this);
		cancelRadiusButton.setOnClickListener(this);
		updateRadiusButton.setOnClickListener(this);
		return view;
	}
	
	interface Communicator
	{
		public void updateRadius(int radius, boolean finish);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		radiusText.setText(""+progress+" feet");
		comm.updateRadius(progress, false);
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.cancelRadiusButton)
		{
			dismiss();
		}
		else if (v.getId() == R.id.updateRadiusButton)
		{
			comm.updateRadius(radiusBar.getProgress(), true);
			dismiss();
		}
		
	}

}
