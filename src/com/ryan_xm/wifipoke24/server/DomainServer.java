package com.ryan_xm.wifipoke24.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ryan_xm.wifipoke24.util.L;
import com.ryan_xm.wifipoke24.util.Utils;

public class DomainServer {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	
	// dynamic configuration
	public static final int MODE_SERVER = 1; // 指定服务端
	public static final int MODE_CLIENT = 2; // 指定客户端
	private int mode = 0;
	
	// controllers
	private static DomainServer INSTANCE = null;
	// 对应网络方面的操作
	private NetServer NET = null;
	// 游戏方面的操作
	private GameServer GAME = null;
	
	
	private Handler handlerDomain;
	// 用户更新界面UI
	private Handler handlerUI;
	
	private Boolean gameRunning = false;
	
	// 游戏名称
	private String serverName; 
	
	private DomainServer() {
		this.handlerDomain = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.getData().containsKey("MSG")) {
					parserController(msg.getData().get("MSG").toString());
				}
			}
		};
	}

	public static DomainServer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DomainServer();
			INSTANCE.NET = NetServer.getInstance();
			INSTANCE.GAME = GameServer.getInstance();
		}
		return INSTANCE;
	}
	
	// 保存用户设置的 游戏名， 组队数， 回合数
	public void setConfCreate(Bundle b) {
		this.serverName = b.getString("servername");
		//this.GAME.setConfCreate(b);
	}
	
	
	private void parserController(String str) {
		L.d(str);
		String[] actionContent = str.split(" ", 2);
		String[] args = null;
		if (actionContent.length > 1) {
			args = actionContent[1].split(",");
		}

		if (actionContent[0].equals("WAITINGROOM")) {
			// The name of the players and the team they belong
			// Only used in the *Waiting classes (JoinGameWaiting and NewGameWaiting)
			// The class sent with the info is WaitingRoom
			WaitingRoom room = (WaitingRoom) unserialize(args[0]);

			// Update the client UI
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString("type", "WAITINGROOM");
			b.putSerializable("room", room);
			msg.setData(b);
			handlerUI.sendMessage(msg);

		} else if (actionContent[0].equals("SHUTDOWN")) {
			// The server is closing the connection

			shutdownUI();
		} else if (actionContent[0].equals("STARTGAME")) {
			// The server started the game
			// Clients must leave the *Waiting view and change it to the Game

			this.gameRunning = true;

			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString("type", "STARTGAME");
			msg.setData(b);
			handlerUI.sendMessage(msg);

		} else if (actionContent[0].equals("PORKS")) {
			// 这里是客户端，收到服务器广播发送的消息了， 这里每个客户端收到后，就把当前的牌保存，并在之后收到“STARTGAME”后开始启动MultiGameActivity
			// The server sent a new board
			// Update the UI
			GAME.setPorks((Porks) unserialize(args[0]));
			
			this.gameRunning = true;
			
//		} 
//		else if (actionContent[0].equals("NEWROUND")) {
//			// The server started the game
//			// Clients must leave the *Waiting view and change it to the Game
//
//			this.gameRunning = true;
//
//			GAME.setPorks((Porks) unserialize(args[0]));
//			
//			Message msg = new Message();
//			Bundle b = new Bundle();
//			b.putString("type", "NEWROUND");
//			msg.setData(b);
//			handlerUI.sendMessage(msg);

		} else if (actionContent[0].equals("OneClientToServer")) { 
			// 设置是单独一个客户端发送过来的消息，然后再服务器端接收后，在分发给所有客户端---- 这里表示一个用户已经回答正确了
			// A client sent a new board
			// Send this new board to all the clients
			String winPlayer = (String) args[0];
			L.d("OneClientToServer, winPlayer="+winPlayer);
			NET.sendUpdatedBoardClients(winPlayer);
			
		} else if (actionContent[0].equals("ServerToAllClient")) { 
			// 这个是服务器给每个客户端发送的消息
			// The server sent a new board
			// Update the UI
			
			GAME.setPorks((Porks) unserialize(args[1]));
			
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString("type", "PLAYER");
			b.putString("player", args[0]);
			msg.setData(b);
			handlerUI.sendMessage(msg);
		} 
		
		
	}
	
	
	
	
	
	
	
	
	//==================================================================
	
	
	// 保存某个界面的handler，用这个handler来通知对应界面的操作
	public void setHandlerUI(Handler hand) {
		this.handlerUI = hand;
	}
	
	public Handler getHandlerDomain() {
		return this.handlerDomain;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	
	//===============Wifi相关操作==================================
	public void setWifiManager(WifiManager systemService) {
		NET.setWifiManager(systemService);
	}
	
	//===============UDP相关操作==================================
	// 对于server端来说，创建游戏，等待连接
	public void serverUDPStart() {
		NET.serverUDPStart();
	}

	// 对于client端来说，搜索游戏
	public void serverUDPFind(Handler handler) {
		NET.serverUDPFind(handler);
	}

	// 对于server & client 都是停止UDP服务
	public void serverUDPStop() {
		NET.serverUDPStop();
	}


	
	//===============TCP相关操作==================================
	// 服务端启动TCP线程
	public void serverTCPStart() throws Exception {
		this.mode = MODE_SERVER; // 指定是服务端启动的
		this.gameRunning = false;
		NET.serverTCPStart();
	}

	// 停止&退出TCP线程
	public void serverTCPStop() {
		NET.serverTCPStop();
	}

	// 客户端获取到服务端的IP地址还有端口号后，就开始发起TCP连接
	public void serverTCPConnect(String serverIP, int serverPort) throws Exception {
		this.mode = MODE_CLIENT; // 指定是客户端启动的
		this.gameRunning = false;
		String result = NET.serverTCPConnect(serverIP, serverPort); // 连接服务端， 服务器会返回分配的playerID, teamID
	}

	public void serverTCPDisconnect() {
		this.mode = 0;
		this.gameRunning = false;
		NET.serverTCPDisconnect();
	}

	public void serverTCPDisconnectClients() {
		this.mode = 0;
		this.gameRunning = false;
		NET.serverTCPDisconnectClients();
	}
	
	
	
	
	
	
	
	//============================================================

	/**
	 * A disconnection has been detected. Notify the UI if in client mode or
	 * notify the rest of the clients if in server mode.
	 * 
	 * @param conn
	 */
	public void disconnectionDetected(TCPConnection conn) {
		conn.close();
		
		if (mode == DomainServer.MODE_CLIENT) {
			// If in client mode, notify the UI about the shutdown
			shutdownUI(); // 一个用户端断开，首先自己的TCPConnection会异常，然后跑到这里去更新UI，就是退出游戏界面
		} else if (mode == DomainServer.MODE_SERVER && !gameRunning) {
			// This happens when there is a disconnection in the WaitingRoom
			NetServer.getInstance().removePlayer(conn); 
			updatedPlayers();
		} else if (mode == DomainServer.MODE_SERVER && gameRunning) {
			// This happens when there is a disconnection while playing a game
			// 一个用户端退出后，会导致服务端保存的对应player的TCPConnection也异常，然后也会跑到这里。
			// 然后再通知所有的player都退出游戏
			NetServer.getInstance().sendShutdown(); 
		}
	}
	
	
	/**
	 * Notifies the UI that some error with the connection occurred
	 */
	public void shutdownUI() {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("type", "SHUTDOWN");
		msg.setData(b);
		handlerUI.sendMessage(msg);
	}

	
	
	/**
	 * Notify all the clients that the WaitingRoom has changed because a new
	 * player joined or a current player disconnected
	 */
	public void updatedPlayers() {
		WaitingRoom r = new WaitingRoom();

		// Send the info to all the connected clients
		NET.sendSignals("WAITINGROOM " + serialize(r));
	}
	
	
	
	public String getPlayerName() {
		return Utils.playerName;
	}
	
	// 获取当前一共有多少个玩家
	public int getPlayerCount() {
		return NET.getInstance().getPlayerCount();
	}
	
	// 发送文本
	public void sendMessage(String text) {
		try {
			this.NET.sendUpdatedBoardServer(text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Starts a new game
	 * 
	 * SinglePlayer : 
	 * Just create a new board and starts the game
	 * 
	 * MultiPlayer:
	 * Send to all the connected clients that the game is going to start. The
	 * clients must be on *Waiting views.
	 */
	public void startGame() {
		//this.GAME.createNewCleanBoard();
		//this.GAME.setPlayers(this.NET.serverTCPGetConnectedPlayersName());
		
		// 随机产生4张牌,并发送给各个客户端
		this.GAME.InitRandomPorks();
		this.NET.sendSignalsStartGame();
	}
	
	/**
	 * The UI wants to close the running game
	 */
	public void stopGame() {
		if (mode == DomainServer.MODE_CLIENT) {
			serverTCPDisconnect(); // 如果客户端，就断开自己的连接
		} else if (mode == DomainServer.MODE_SERVER) {
			serverTCPDisconnectClients(); // 如果是服务端，则断开所有client
		}
	}
	
	
	
	// -----------------------------------------------------------------
	// move?
	public String serialize(Object object) {
		byte[] result = null;

		try {
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			os.writeObject(object);
			os.close();
			result = bs.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return new String();
		}

		return Base64.encodeToString(result, Base64.NO_WRAP);
	}

	// move?
	public Object unserialize(String str) {
		Object object = null;
		byte[] bytes = Base64.decode(str, Base64.NO_WRAP);

		try {
			ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
			ObjectInputStream is = new ObjectInputStream(bs);
			object = (Object) is.readObject();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new String();
		}
		return object;
	}
	
	
	
	
}
