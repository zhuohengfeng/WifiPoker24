package com.ryan_xm.wifipoke24.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.server.DomainServer;
import com.ryan_xm.wifipoke24.server.WaitingRoom;
import com.ryan_xm.wifipoke24.util.L;

public class NewGameWaitingActivity extends Activity {
	
	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	private WaitingRoom room;

	// 在创建游戏的等待界面，用handler来操作UI
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Bundle b = msg.getData();
			String type = b.getString("type");

			if (type.equals("WAITINGROOM")) {
				updateWaitingRoom(b); // 每次有新玩家加入，都会用一个WaitingRoom数据结构来通知所有玩家(包括服务器端的player)
			} else if (type.equals("STARTGAME")) { 
				startGame(); // 用户选择开始游戏
			} else if (type.equals("SHUTDOWN")) {
				// 断开服务器
				Toast.makeText(getBaseContext(), "The server was closed", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gamewaiting);

		// 用户点击“开始游戏”
		Button b;
		b = (Button) findViewById(R.id.Start);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startGameButton();
			}
		});
		
		// 用户点击“返回”
		b = (Button) findViewById(R.id.Back);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DomainServer.getInstance().serverTCPDisconnectClients();
				finish();
			}
		});
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		L.e("start");
		
		// 这里把这个界面的handler传入，当有状态改变时，通过这个handler来改变此界面的UI状态
		DomainServer.getInstance().setHandlerUI(handler);

		try {
			// 用户启动TCP线程
			DomainServer.getInstance().serverTCPStart();
			// 用户启动UDP线程
			DomainServer.getInstance().serverUDPStart();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), "Couldn't create the server", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		L.e("end");
	}

	@Override
	protected void onStop() {
		super.onStop();
		DomainServer.getInstance().serverUDPStop();
		DomainServer.getInstance().serverTCPStop();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 用户按返回，就断开TCP连接
			DomainServer.getInstance().serverTCPDisconnectClients();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Parse a newly received WaitingRoom class
	 * 
	 * @param b Bundle containing the WaitingRoom class
	 */
	private void updateWaitingRoom(Bundle b) {
		L.e("updateWaitingRoom");
		
		this.room = (WaitingRoom) b.getSerializable("room");

		TextView tv;
		tv = (TextView) findViewById(R.id.WaitingServername);
		tv.setText(String.valueOf(room.name));
	
		LinearLayout ll = (LinearLayout) findViewById(R.id.Players);
		ll.removeAllViews();
		
		for (int i = 0; i < room.players.size(); i++) {
			View child = getLayoutInflater().inflate(R.layout.row_player, null);
			tv = (TextView) child.findViewById(R.id.Player);
			tv.setText(String.valueOf(room.players.get(i).name));
			ll.addView(child);
		}
	}

	/**
	 * Called by the button listener. The server is going to send the signal
	 * about the start of the game.
	 */
	private void startGameButton() {
		if (this.room != null && this.room.players.size() > 1) {
			DomainServer.getInstance().startGame();
		} else {
			Toast.makeText(getBaseContext(), R.string.game_err_noenough_player, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 服务端启动游戏, 每个palyer都会调用这个函数去启动，包括服务器端的player
	 * Change the view to Game because the server started the game
	 */
	protected void startGame() {
		Intent i = new Intent();
		i.setClass(getBaseContext(), MultiGameActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		finish();
	}
}
