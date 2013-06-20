package com.example.brightcovetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends Activity {

	private Button submitBtn;
	private Button registerBtn;
	private EditText usernameEt;
	private EditText passwordEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_screen);
		submitBtn = (Button) findViewById(R.id.sign_in_submit);
		registerBtn = (Button) findViewById(R.id.sign_in_register);
		usernameEt = (EditText) findViewById(R.id.sign_in_username);
		passwordEt = (EditText) findViewById(R.id.sign_in_password);
		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (usernameEt != null) {
					if (LoginManager.login(usernameEt.getText().toString(), passwordEt.getText().toString())) {
                        Toast.makeText(SigninActivity.this, "WELCOME: "+usernameEt.getText().toString(), Toast.LENGTH_SHORT).show();
						finishSuccessfully();
					}
					else{
						Toast.makeText(SigninActivity.this, "SIGN IN UNSUCESSFULL", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (usernameEt != null) {
					if (LoginManager.register(usernameEt.getText().toString(), passwordEt.getText().toString())) {
                        Toast.makeText(SigninActivity.this, "WELCOME: "+usernameEt.getText().toString(), Toast.LENGTH_SHORT).show();
                        finishSuccessfully();
					}
					else{
						Toast.makeText(SigninActivity.this, "REGISTRATION UNSUCESSFULL", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	protected void finishSuccessfully() {
		Intent returnIntent = new Intent();
		setResult(Activity.RESULT_OK,returnIntent);
		super.finish();
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(Activity.RESULT_CANCELED,returnIntent);
		super.onBackPressed();
	};
}
