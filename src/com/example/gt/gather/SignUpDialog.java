package com.example.gt.gather;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpDialog extends DialogFragment implements View.OnClickListener{
	
	EditText emailEdit;
	EditText pwEdit;
	EditText pwConfirmEdit;
	Button signUpButton;
	Communicator comm;
	String userName, password;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		comm = (Communicator) getActivity();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.signup_dialog, null);
		emailEdit = (EditText) view.findViewById(R.id.emailEditText);
		pwEdit = (EditText) view.findViewById(R.id.pwEditText);
		pwConfirmEdit = (EditText) view.findViewById(R.id.pwConfirmEditText);
		signUpButton = (Button) view.findViewById(R.id.signUpButton);
		setCancelable(false);
		signUpButton.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.signUpButton)
		{
			userName = emailEdit.getText().toString();
			password = pwEdit.getText().toString();
			String confirmpw = pwConfirmEdit.getText().toString();
			
			if (!userName.contains("@gatech.edu"))
			{
				Toast.makeText(getActivity(), "Invalid Email. Must be a Georgia Tech email.", Toast.LENGTH_LONG).show();
				emailEdit.setText("");
				pwEdit.setText("");
				pwConfirmEdit.setText("");
			}
			else if (!password.equals(confirmpw))
			{
				Toast.makeText(getActivity(), "You're passwords did not match. Try again.", Toast.LENGTH_LONG).show();
				pwEdit.setText("");
				pwConfirmEdit.setText("");
			}
			else
			{
				comm.createUser(userName, password);
			}
		}
	}
	
	interface Communicator {
		public void createUser(String userName, String password);
	}
}
