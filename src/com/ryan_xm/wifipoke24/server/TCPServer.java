package com.ryan_xm.wifipoke24.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import com.ryan_xm.wifipoke24.util.L;

public class TCPServer extends Thread {
	
	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	ServerSocket serverSocket;

	private Vector<Player> players;

	private Integer numPlayerLastAssigned;

	private Boolean keepRunning;
	private Boolean listening;
	
	public TCPServer(Vector<Player> players) {
		super();

		setName("TCPServer");

		this.players = players;

		this.numPlayerLastAssigned = 0;

		this.keepRunning = true;
		this.listening = false;
	}
	
	public void run() {
	
		try {

			L.d("first line");

			NetServer.getInstance().portServer = NetServer.TCP_PORT;

			do {

				try {
					// 首先创建一个ServerSocket, 这里是循环端口创建
					serverSocket = new ServerSocket(NetServer.getInstance().portServer);
				} catch (Exception e) {
					NetServer.getInstance().portServer++;
					L.d("Trying with " + NetServer.getInstance().portServer);
				}

			} while (serverSocket == null || !serverSocket.isBound());

			L.d("TCPServer Started");

			this.listening = true;
			
			// 服务器等待客户端的tcp连接
			while (keepRunning) {
				Socket client = serverSocket.accept();
				// 创建一个角色player
				Player new_player = new Player(new TCPConnection(client), this.numPlayerLastAssigned);
				this.numPlayerLastAssigned++;
				players.add(new_player);
				DomainServer.getInstance().updatedPlayers();
			}
		} catch (Exception e) {
			L.e("Exception  in running TCPServer", e);
		}
	}

	public void close() {
		try {
			this.keepRunning = false;
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Boolean isListening() {
		return this.listening;
	}

}