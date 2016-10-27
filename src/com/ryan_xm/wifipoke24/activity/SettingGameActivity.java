package com.ryan_xm.wifipoke24.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.util.Utils;

public class SettingGameActivity extends Activity {

	private EditText mPlayerName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting);
		
		String playerName = Utils.readPlayerName(this);
		
		mPlayerName = (EditText)findViewById(R.id.PlayerName);
		mPlayerName.setText(playerName);
		
		Button mConfirm = (Button)findViewById(R.id.confirm);
		mConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String playername = mPlayerName.getText().toString().trim();
				Utils.savePlayerName(SettingGameActivity.this, playername);
				finish();
			}
		});
	}
	
	
	
	
}
