package com.ryan_xm.wifipoke24.server;

import java.io.Serializable;
import java.util.Vector;


// 一个WaitingRoom,可以理解成为一个"新游戏"，每次有新玩家加入，就用这个数据结构通知所有的player，包括服务器端那个player.
// 而一个"新游戏"中，游戏名，设置的组对数，回合数都是设置好的，大家都是一样的
// 而一个新游戏中有多少"正在等待的玩家"则是变化的，需要用一个Vector<WaitingRoomPlayer>来保存，所以也需要定义成Serializable
public class WaitingRoom implements Serializable {

	private static final long serialVersionUID = -1265117283783770369L;

	public String name;
	public Vector<WaitingRoomPlayer> players;

	// Dynamic configuration about the player
	// MUST be blank when sending from the server
	// It will be populated when received by the client before sending it to the UI 

	public WaitingRoom() {

		DomainServer DOM = DomainServer.getInstance();
		NetServer NET = NetServer.getInstance();

		// Parse the information from the controllers
		this.name = DOM.getServerName();
		
		// And the players connected
		this.players = new Vector<WaitingRoomPlayer>(NET.serverTCPGetConnectedPlayersNum());

		Vector<String> players_name = NET.serverTCPGetConnectedPlayersName();

		for (int i = 0; i < NET.serverTCPGetConnectedPlayersNum(); i++) {
			WaitingRoomPlayer new_player = new WaitingRoomPlayer(players_name.get(i));
			this.players.add(new_player);
		}
	}

	public class WaitingRoomPlayer implements Serializable {
		private static final long serialVersionUID = 8440232224311331235L;

		public String name;

		public WaitingRoomPlayer(String name) {
			super();
			this.name = name;
		}
	}
}
