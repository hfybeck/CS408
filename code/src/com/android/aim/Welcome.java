package com.android.aim;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_page);
	}
	
	public void startSignUp(View v){
		try{
			Intent signup = new Intent(this, SignUp.class);
			startActivity(signup);
			finish();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startLogin(View v){
		try{
			Intent login = new Intent(this, Login.class);
			startActivity(login);
			finish();
		}
		catch (Exception e) {
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.welcome_page, menu);
		return true;
	}

}
