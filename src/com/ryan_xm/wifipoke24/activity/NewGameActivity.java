package com.ryan_xm.wifipoke24.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ryan_xm.wifipoke24.R;
import com.ryan_xm.wifipoke24.server.DomainServer;

public class NewGameActivity extends Activity {
	
	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newgame);
        
        final Button button = (Button) findViewById(R.id.Create); // 创建游戏
        final Button cancel = (Button) findViewById(R.id.Back); // 后退
        
		final EditText textNameServer = (EditText) findViewById(R.id.NewGameServername);
		textNameServer.setText("WifiGame");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// 把用户输入的 游戏名，组队数，回合数 存起来
            	Bundle b = new Bundle();
				b.putString("servername", textNameServer.getText().toString());
				DomainServer.getInstance().setConfCreate(b);

				// 进入游戏等待界面，等待客户端连接
                Intent i = new Intent();
				i.setClass(v.getContext(), NewGameWaitingActivity.class);
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			startActivity(i);
				finish();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });

    }
}
