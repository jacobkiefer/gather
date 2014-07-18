package com.example.gt.gather;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewEventDialog extends DialogFragment implements View.OnClickListener {
	
	Button broadcastButton;
	EditText eventName;
	TextView eventDate;
	ImageView dateImageButton;
	ImageView timeImageButton;
	TextView eventTime;
	EditText eventNumPeople;
	Communicator comm;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		comm = (Communicator) activity;
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("Create New Event");
		View view = inflater.inflate(R.layout.new_event_dialog, null);
		broadcastButton = (Button) view.findViewById(R.id.broadcastButton);
		eventName = (EditText) view.findViewById(R.id.nameEditText);
		eventDate = (TextView) view.findViewById(R.id.dateView);
		eventTime = (TextView) view.findViewById(R.id.timeView);
		eventNumPeople = (EditText) view.findViewById(R.id.numPeopleEditText);
		dateImageButton = (ImageView) view.findViewById(R.id.dateButton);
		timeImageButton = (ImageView) view.findViewById(R.id.timeButton);
		broadcastButton.setOnClickListener(this);
		dateImageButton.setOnClickListener(this);
		timeImageButton.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.broadcastButton)
		{	
			String name = eventName.getText().toString();
			String date = eventDate.getText()+" "+eventTime.getText();
			int numPeople = 0;
			try
			{
				numPeople = Integer.parseInt(eventNumPeople.getText().toString());
			}
			catch (NumberFormatException ex){}
		
			comm.onBroadcastMessage(name, date, numPeople);
			dismiss();
		}
		else if(v.getId() == R.id.dateButton)
		{
			DatePickerFragment dateFrag = new DatePickerFragment();
			dateFrag.show(getActivity().getFragmentManager(), "DatePicker");
		}
		else if(v.getId() == R.id.timeButton)
		{
			TimePickerFragment timeFrag = new TimePickerFragment();
			timeFrag.show(getActivity().getFragmentManager(), "TimePicker");
		}
	}
	
	public void setDate(int year, int month, int day){
		String monthOfYear = ""+month;
		String dayOfMonth = ""+day;
		if(month < 10)
		{
			monthOfYear = "0"+month;
		}
		if(day <10)
		{
			dayOfMonth = "0"+day;
		}
		eventDate.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
	}
	
	public void setTime(int hour, int min) {
		String am_pm = "";
        if (hour > 12) {
            hour -= 12;
            am_pm = "PM";
        } else if (hour == 0) {
            hour += 12;
            am_pm = "AM";
        } else if (hour == 12)
        	am_pm = "PM";
        else
        	am_pm = "AM";
 
         
        String minutes = "";
        if (min < 10)
            minutes = "0" + min;
        else
            minutes = String.valueOf(min);
		
		eventTime.setText(""+hour+":"+minutes+" "+am_pm);		
	}
	
	interface Communicator
	{
		public void onBroadcastMessage(String name, String date, int numPeople);
	}

}
