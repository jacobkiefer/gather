package com.example.gt.gather;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener{
	
	Communicator comm;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		comm = (Communicator) getActivity();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// use current time as default values:
		final Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		
		// Create new instance of TimePickerDialog and return it
		
		return new TimePickerDialog(getActivity(), this, hour, minute, false);
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int min) {
		comm.setTime(hourOfDay, min);	
	}
	
public interface Communicator
{
	public void setTime(int hour, int min);
}

}
