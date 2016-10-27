package com.ryan_xm.wifipoke24.server;

import com.ryan_xm.wifipoke24.util.L;


// 这里的Player其实是在服务端维护的，当有一个玩家连接服务器，这就给每个玩家分配一个TCPConnection线程
// 然后立即给player发送numPlayer和numTeam
public class Player {

	private String name;
	private Integer numPlayer;
	private TCPConnection connection;

	public Player(TCPConnection connection, Integer numPlayer) throws Exception {
		this.connection = connection;
		// The first thing we receive is the player name
		this.name = in();
		this.numPlayer = numPlayer;

		// 给客户端发送numPlayer和numTeam
		L.d("Now send the numPlayer:"+this.numPlayer.toString());
		// The first thing we send is the player id and the team id assigned
		out(this.numPlayer.toString());

		this.connection.setName("TCP server " + this.name);
		this.connection.start(); // 启动线程读取消息
	}

	/*
	 * getters
	 */
	public String getName() { 
		return name; 
	}
	public Integer getNumPlayer() { 
		return numPlayer; 
	}
	public String in() throws Exception { 
		return connection.in(); 
	}
	public void out(String content) throws Exception { 
		connection.out(content); 
	}
	public TCPConnection getConnection() { 
		return connection; 
	}

	public void close() {
		if (this.connection != null && this.connection.isAlive()) {
			this.connection.close();
			while (this.connection.isAlive()) {
				;
			}
		}
		this.connection = null;
	}
}
