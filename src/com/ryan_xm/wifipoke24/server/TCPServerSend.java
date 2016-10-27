package com.ryan_xm.wifipoke24.server;

import java.util.Vector;

import com.ryan_xm.wifipoke24.util.L;

// 这个线程用于服务器给所有的player发送消息
public class TCPServerSend extends Thread {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private Vector<Player> players;
	private String data;
	private Vector<String> data_vector;
	private Integer mode;

	public TCPServerSend(Vector<Player> players, String data) {
		super();

		setName("TCPServerSend");

		this.players = players;
		this.data = data;

		this.mode = 1;
	}

	public TCPServerSend(Vector<Player> players, Vector<String> data) {
		super();

		setName("TCPServerSend");

		this.players = players;
		this.data_vector = data;

		this.mode = 2;
	}

	public TCPServerSend(Vector<Player> players) {
		super();

		setName("TCPServerSend");

		this.players = players;

		this.mode = 3;
	}

	public void run() {

		if (mode == 1) {
			//Just send one thing

			for (int i = (this.players.size() - 1); i >= 0; i--) {
				try {
					L.d("sending to player " + i + " " + data);
					this.players.get(i).out(data);
				} catch (Exception e) {

					// If the data we are sending is SHUTDOWN ignore the closed sockets and continue sending
					if (!data.equals("SHUTDOWN")) {
						DomainServer.getInstance().disconnectionDetected(this.players.get(i).getConnection());
					}
				}
			}

		} else if (mode == 2) {
			// Send more than one thing

			for (int d = 0; d < data_vector.size(); d++) {
				for (int i = (this.players.size() - 1); i >= 0; i--) {
					try {
						L.d("sending to player " + i + " " + data_vector.get(d));
						this.players.get(i).out(data_vector.get(d));
					} catch (Exception e) {
						DomainServer.getInstance().disconnectionDetected(this.players.get(i).getConnection());
					}
				}
			}

		} else if (mode == 3) { 
			// 开始游戏相关的命令发送， 这里是服务器端发送的
			
			// Send the startGame
			//String board = "UPDATEBOARD " + DomainServer.getInstance().serialize(DomainServer.getInstance().getBoardToSend());
			// 开始游戏，发送随机产生的4张牌
			String board = "PORKS " + DomainServer.getInstance().serialize(GameServer.getInstance().getPorks());

			for (int i = 0; i < this.players.size(); i++) {
				try {
					L.d("sending to player " + i + " " + board);
					this.players.get(i).out(board);
				} catch (Exception e) {
					DomainServer.getInstance().disconnectionDetected(this.players.get(i).getConnection());
				}
			}

			// TODO: Ugly fix, wait between sending packets
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
			}
			
			
			String text_to_start = "STARTGAME";

			for (int i = 0; i < this.players.size(); i++) {
				try {
					L.d("Sending to player " + i + " STARTGAME");
					this.players.get(i).out(text_to_start);
				} catch (Exception e) {
					DomainServer.getInstance().disconnectionDetected(this.players.get(i).getConnection());
				}
			}
			
		}
	}
}