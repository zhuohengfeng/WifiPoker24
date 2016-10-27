package com.ryan_xm.wifipoke24.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.server.DomainServer;
import com.ryan_xm.wifipoke24.util.Utils;

public class WelcomeActivity extends Activity {

	private NetworkInfo mWifi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.readPlayerName(this);
		
		DomainServer.getInstance().setWifiManager((WifiManager) getSystemService(Context.WIFI_SERVICE));
		
		setContentView(R.layout.welcome);
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		
		final Intent intent = new Intent();
		
		Button mCreateGame = (Button) this.findViewById(R.id.createGameBtn);
		mCreateGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mWifi.isConnected()) {
				    Toast.makeText(WelcomeActivity.this, R.string.game_err_no_wifi, Toast.LENGTH_SHORT).show();
				    return;
				}
				
				intent.setClass(v.getContext(), NewGameActivity.class);
    			startActivity(intent);
			}
		});
		
		
		Button mJoinGame = (Button) this.findViewById(R.id.joinGameBtn);
		mJoinGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mWifi.isConnected()) {
				    Toast.makeText(WelcomeActivity.this, R.string.game_err_no_wifi, Toast.LENGTH_SHORT).show();
				    return;
				}
				
				intent.setClass(v.getContext(), JoinGameActivity.class);
    			startActivity(intent);
			}
		});
		
		
		Button mSingleGame = (Button) this.findViewById(R.id.singleGameBtn);
		mSingleGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.setClass(v.getContext(), SingleGameActivity.class);
    			startActivity(intent);
			}
		});
		
		
		Button mSettingGame = (Button) this.findViewById(R.id.settingGameBtn);
		mSettingGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.setClass(v.getContext(), SettingGameActivity.class);
    			startActivity(intent);
			}
		});
		
	}
	

	
}