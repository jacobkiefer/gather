package com.example.gt.gather;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialog extends DialogFragment implements OnClickListener{
	
	EditText emailEdit;
	EditText pwEdit;
	TextView signupText;
	Button loginButton;
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
		View view = inflater.inflate(R.layout.login_dialog, null);
		emailEdit = (EditText) view.findViewById(R.id.emailEditText);
		pwEdit = (EditText) view.findViewById(R.id.pwEditText);
		signupText = (TextView) view.findViewById(R.id.signUp);
		loginButton = (Button) view.findViewById(R.id.loginButton);
		setCancelable(false);
		loginButton.setOnClickListener(this);
		signupText.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.loginButton)
		{
			userName = emailEdit.getText().toString();
			password = pwEdit.getText().toString();
			
			if (!userName.contains("@gatech.edu"))
			{
				Toast.makeText(getActivity(), "Invalid Email. Must be a Georgia Tech email.", Toast.LENGTH_LONG).show();
				emailEdit.setText("");
				pwEdit.setText("");
			}
			else
			{
				comm.verifyAndLogin(userName, password);
			}
		}
		else if(v.getId()==R.id.signUp)
		{
			comm.showSignUpDialog();
			dismiss();
		}
		
	}
	
	interface Communicator {
		public void verifyAndLogin(String userName, String password);
		public void onLogIn();
		public void showSignUpDialog();
	}


}
