package com.example.gt.gather;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class JoinEventDialog extends DialogFragment implements View.OnClickListener {

	static JoinEventDialog newInstance(String name, String date, int numPeople, String id) {
	    JoinEventDialog joinFrag = new JoinEventDialog();

	    // Supply num input as an argument.
	    Bundle args = new Bundle();
	    args.putString("name", name);
	    args.putString("date", date);
	    args.putInt("numPeople", numPeople);
	    args.putString("id", id);
	    joinFrag.setArguments(args);
	    
	    return joinFrag;
	}
	
	Button joinButton;
	Button cancelButton;
	TextView eventDate;
	TextView eventDistance;
	TextView eventNumPeople;
	Communicator comm;
	String name;
	String date;
	String id;
	int numPeople;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		name = getArguments().getString("name");
		date = getArguments().getString("date");
		numPeople = getArguments().getInt("numPeople");
		id = getArguments().getString("id");
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		comm = (Communicator) activity;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("Join "+name);
		View view = inflater.inflate(R.layout.join_event_dialog, null);
		joinButton = (Button) view.findViewById(R.id.joinButton);
		cancelButton = (Button) view.findViewById(R.id.cancelButton);
		eventDate = (TextView) view.findViewById(R.id.dateView);
		eventDistance = (TextView) view.findViewById(R.id.addressView);
		eventNumPeople = (TextView) view.findViewById(R.id.numPeopleView);
		joinButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
		eventDate.setText(date);
		eventNumPeople.setText(""+numPeople);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.joinButton)
		{	
			comm.join(id, numPeople-1);
			dismiss();
		}
		else if(v.getId() == R.id.cancelButton)
		{
			dismiss();
		}
	}
	
	interface Communicator
	{
		public void join(String id, int newNumPeople);
	}

}
