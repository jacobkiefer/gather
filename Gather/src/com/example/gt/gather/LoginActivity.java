package com.example.gt.gather;

import com.google.android.gms.common.SignInButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class LoginActivity extends Activity implements OnClickListener{
	
	SignInButton googleSignIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Intent i = getIntent();
		googleSignIn = (SignInButton) findViewById(R.id.googleSignIn);
		googleSignIn.setSize(SignInButton.SIZE_ICON_ONLY);
		googleSignIn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent loginReply = new Intent();
		loginReply.putExtra("LoginType", "Google");
		loginReply.putExtra("isLoggedIn", true);
		setResult(100, loginReply);
		finish();
		
	}
	
	

}
