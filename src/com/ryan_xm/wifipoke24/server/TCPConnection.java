package com.ryan_xm.wifipoke24.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Bundle;
import android.os.Message;

import com.ryan_xm.wifipoke24.util.L;


// 这个是线程是跑在客户端的
public class TCPConnection extends Thread {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private Socket socket;

	private Boolean keepRunning;

	// 客户端连接
	public TCPConnection(String ip, int port) {
		super();

		try {
			this.socket = new Socket(ip, port);
			this.keepRunning = true;

		} catch (IOException e) {
			// TODO: error when establishing a connection?
			e.printStackTrace();
		}
	}

	// 这个是服务端保存的
	public TCPConnection(Socket socket) {
		this.socket = socket;

		this.keepRunning = true;
	}

	@Override
	public void run() {
		super.run();

		try {
			String received;
			// 注意这里in()也是阻塞式的，所以不会一直循环跑，而是等待发送过来的命令消息
			while (keepRunning && (received = in()) != null) {
				sendMsg("MSG", received);
			}
			L.e("TCPConnection thread run exit---");
			// 这里是一个客户端断开后，服务端这边会in()为空
			if (keepRunning) {
				L.e("CONNECTION READ NULL");
				// 如果没有读到任何消息，则断开连接，并通知所有剩余的playui退出
				DomainServer.getInstance().disconnectionDetected(this);
			}

		} catch (Exception e) {
			L.e("CONNECTION READ EXCEPTION");
			DomainServer.getInstance().disconnectionDetected(this);
		}

	}

	public String in() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String result = in.readLine();
		return result;
	}

	public void out(String content) throws Exception {
		PrintWriter out;
		out = new PrintWriter(socket.getOutputStream(), true);
		out.println(content);
	}

	// 如果收到消息，则通过handler来通知domain
	private void sendMsg(String type, String content) {
		L.e("sendMsg type："+type+", content="+content);
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString(type, content);
		msg.setData(data);
		DomainServer.getInstance().getHandlerDomain().sendMessage(msg);
	}

	public void close() {
		L.d("Closing TCPConnection");
		this.keepRunning = false;
		try {
			this.socket.shutdownInput();
		} catch (IOException e) {
		}

		try {
			this.socket.close();
		} catch (IOException e) {
		}
	}


}
