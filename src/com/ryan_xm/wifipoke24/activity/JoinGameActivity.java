package com.ryan_xm.wifipoke24.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.server.DomainServer;

public class JoinGameActivity extends Activity {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private LinearLayout ll;
	private int color = 0x0F000000;
	
	private Handler udpHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			// 当客户端搜索到一个新游戏后，列表显示游戏信息(游戏名称， 主机名， 主机端口)。
			// 在UDP线程中就是客户端收到服务端点对点发送的信息之后更新
			View child = getLayoutInflater().inflate(R.layout.row_join, null);
			child.setTag( (Bundle) msg.getData() );

			TextView tv;
			tv = ((TextView) child.findViewById(R.id.Name));
			tv.setText( msg.getData().getString("NAME") );
			tv.setBackgroundColor(color);
			tv = ((TextView) child.findViewById(R.id.Ip));
			tv.setText( msg.getData().getString("IP") );
			tv = ((TextView) child.findViewById(R.id.Port));
			tv.setText( String.valueOf(msg.getData().getInt("PORT")) );

			child.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					view.setBackgroundColor(Color.YELLOW);
					Intent i = new Intent();
					i.setClass(view.getContext(), JoinGameWaitingActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtras( (Bundle)view.getTag() );
					startActivity(i);
					finish();
				}
			});
			
			ll.addView( child );
		}
	};


	/**
	 * Reference: http://developer.android.com/images/activity_lifecycle.png
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.joingame);
		
		// 用一个线性列表把搜索到的新游戏加入
		ll = ((LinearLayout) findViewById(R.id.Root));

		Button button;
		// 刷新按钮， 刷新新的游戏
		button = (Button) findViewById(R.id.Refresh);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ll.removeAllViews();
				// 通过UDP来搜索游戏
				DomainServer.getInstance().serverUDPFind(udpHandler);
			}
		});
		
		button = (Button) findViewById(R.id.Back);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * Reference: http://developer.android.com/images/activity_lifecycle.png
	 */
	@Override
	protected void onStart() {
		super.onStart();
		ll.removeAllViews();
		// 通过UDP来搜索游戏
		DomainServer.getInstance().serverUDPFind(udpHandler);
		// CtrlDomain.getInstance().createCleanBoard();
	}

	/**
	 * Reference: http://developer.android.com/images/activity_lifecycle.png
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// 停止UDP搜索游戏
		DomainServer.getInstance().serverUDPStop();
	}
}
