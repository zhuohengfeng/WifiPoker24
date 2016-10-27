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

// 这个是加入游戏后的等待界面， 和创建新游戏一样的界面，只是没有start按钮
public class JoinGameWaitingActivity extends Activity {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Bundle b = msg.getData();
			String type = b.getString("type");

			if (type.equals("WAITINGROOM")) {
				updateWaitingRoom(b);
			} else if (type.equals("STARTGAME")) {// 服务发送开始游戏的消息
				startGame();
			} else if (type.equals("SHUTDOWN")) { // 服务器发送关闭连接的消息
				Toast.makeText(getBaseContext(), "The server closed the connection", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};

	/**
	 * Reference: http://developer.android.com/images/activity_lifecycle.png
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gamewaiting);

		Bundle bundle = this.getIntent().getExtras();
		String serverName = bundle.getString("NAME");
		String serverIP = bundle.getString("IP"); // 这里获取的是服务器端的IP
		int serverPort = bundle.getInt("PORT");
		
		try {
			// client点击某个具体游戏后才开始TCP连接
			DomainServer.getInstance().setHandlerUI(handler);
			DomainServer.getInstance().serverTCPConnect(serverIP, serverPort);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Couldn't connect to the server "+serverName, Toast.LENGTH_SHORT).show();
			finish();
		}

		Button b;
		b = (Button) findViewById(R.id.Back);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DomainServer.getInstance().serverTCPDisconnect();
				finish();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DomainServer.getInstance().serverTCPDisconnect();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Parse a newly received WaitingRoom class
	 * 
	 * @param b
	 *            Bundle containing the WaitingRoom class
	 */
	protected void updateWaitingRoom(Bundle b) {

		WaitingRoom room = (WaitingRoom) b.getSerializable("room");

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

