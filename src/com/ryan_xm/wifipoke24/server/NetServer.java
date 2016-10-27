package com.ryan_xm.wifipoke24.server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;

import android.net.wifi.WifiManager;
import android.os.Handler;

import com.ryan_xm.wifipoke24.util.L;


public class NetServer {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	// 单实例
	private static NetServer INSTANCE = null;
	
	// 组播的IP地址
	public static String UDP_IP = "239.9.9.1";
	// 组播的Port
	public static Integer UDP_PORT = 5761;//17375;
	
	// tcp的Port
	public static Integer TCP_PORT = 6761;//17375;
	
	// 为了避免端口被占用，这里定义一个变量可以循环搜索可以试用的端口
	public Integer portServer = TCP_PORT;
	
	// UDP线程
	private UDPServer threadUDPServer;
	private TCPServer threadTCPServer;
	private TCPConnection threadTCPClient;
	
	// wifi管理器
	private WifiManager wifiManager;
	
	// 保存玩家
	private Vector<Player> players; 
	
	
	// 单实例
	private NetServer() {
		this.players = new Vector<Player>();
	}
	
	public static NetServer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NetServer();
		}
		return INSTANCE;
	}
	
	
	public int getPlayerCount() {
		return players.size();
	}

	
	//===============Wifi相关操作===============================
	public void setWifiManager(WifiManager systemService) {
		this.wifiManager = systemService;
	}
	
	
	// 获取本机的IP地址
	public InetAddress getLocalAddress() throws IOException {
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
					return inetAddress;
				}
			}
		}
		return null;
	}
	
	//=================UDP相关操作==============================
	// 对于server端来说，创建游戏，等待连接
	public void serverUDPStart() {
		serverUDPStop();
		
		// 创建server端的UDP线程
		L.d("server UDP Start running--->");
		this.threadUDPServer = new UDPServer(UDPServer.MODE_SERVER, null);
		this.threadUDPServer.start();
	}

	// 对于client端来说，搜索游戏
	public void serverUDPFind(Handler handler) {
		// If a previous server is already running, stop it
		serverUDPStop();

		L.d("client UDP Start running--->");
		this.threadUDPServer = new UDPServer(UDPServer.MODE_CLIENT, handler);
		this.threadUDPServer.start();
		
		
		L.d("client Sent PING--->");
		this.threadUDPServer.sendBroadcast("PING");
	}

	// 对于server & client 都是停止UDP服务, close之后一直等待线程 not alive!
	public void serverUDPStop() {
		if (this.threadUDPServer != null && this.threadUDPServer.isAlive()) {
			this.threadUDPServer.close();
			while (this.threadUDPServer.isAlive()) {
				;
			}
		}
		this.threadUDPServer = null;
		L.d("UPD Server Closed!!!!");
	}

	

	//===============TCP相关操作===============================
	public void serverTCPStart() throws Exception {

		// Clear previous connections (if any)
		L.d("First stop the TCP connect--------");
		serverTCPStop();
		// 服务器断开所有玩家的TCP连接
		serverTCPDisconnectClients();

		this.players = new Vector<Player>();

		// Creating server
		this.threadTCPServer = new TCPServer(players);
		this.threadTCPServer.start();

		L.d("thread started");

		// Waiting for the server to start
		while (!this.threadTCPServer.isAlive()) {
			;
		}
		L.d("thread alive");
		while (!this.threadTCPServer.isListening()) {
			;
		}
		L.d("thread listening");

		// Connecting like a normal client
		// 这里其实就是把服务器也当作一个玩家，加入。 这样在新游戏等待界面就会显示当前游戏的信息
		serverTCPConnect("127.0.0.1", TCP_PORT); 

		L.d("connected");
	}

	public void serverTCPStop() {
		L.d("TCPServer starting close");

		if (this.threadTCPServer != null && this.threadTCPServer.isAlive()) {
			this.threadTCPServer.close();
			while (this.threadTCPServer.isAlive()) {
				;
			}
		}
		this.threadTCPServer = null;
		L.d("TCPServer Closed");
	}
	
	
	/** 
	 * Connects to the specified server.
	 * 
	 * @param ip
	 *            The IP of the server
	 * @param port
	 *            The port of the server
	 * @return The player id and the team id assigned by the server as a string
	 *         like 7|3
	 * @throws Exception
	 */
	public String serverTCPConnect(String ip, int port) throws Exception {

		// Disconnect from previous server (just in case)
		serverTCPDisconnect(); 

		// 每个客户端都保存有一个TCP Connection的线程，用于连接服务器。。。注意，这里服务器自己也是一个客户端，所以也包含了一个TCPConnection
		this.threadTCPClient = new TCPConnection(ip, port);
		// 线程名称， 这里是客户端，所以知道自己玩家的名称
		this.threadTCPClient.setName("TCP client " + DomainServer.getInstance().getPlayerName());
		// The first thing we send is the player name
		// 连接后，向主机服务器发送玩家名称
		L.d("Send to Server the Player Name: "+ DomainServer.getInstance().getPlayerName());
		this.threadTCPClient.out(DomainServer.getInstance().getPlayerName());
		// The first thing we receive is the player id --- 阻塞读取id
		L.d("read the player_assigned_info start");
		String player_assigned_info = this.threadTCPClient.in();
		L.d("read the player_assigned_info OK  player_assigned_info="+player_assigned_info);
		
		this.threadTCPClient.start();

		return player_assigned_info;
	}

	/** 客户端某个具体玩家去断开连接 */ 
	public void serverTCPDisconnect() {
		L.d("Disconnecting TCP");
		if (this.threadTCPClient != null && this.threadTCPClient.isAlive()) {
			// 调用客户端的close函数
			this.threadTCPClient.close(); 
			while (this.threadTCPClient.isAlive()) {
				;
			}
		}
		this.threadTCPClient = null;
	}

	/** 服务端断开所有玩家连接 */ 
	public void serverTCPDisconnectClients() {

		L.d("Starting disconnection");
		// Close all the remaining connections
		for (Player p : players) {
			L.d("inside for");
			p.close();
		}
		
		L.d("Closed all sockets, clearing...");

		players.clear();

		L.d("Disconnection ended");
	}


	
	
	//================================================
	
	/**
	 * ONLY call this function when a closed socket is found (exception),
	 * because it will only remove the player from the game, it WON'T close the
	 * connection.
	 */
	public void removePlayer(TCPConnection c) {

		for (Player p : players) {
			if (p.getConnection().equals(c)) {
				this.players.remove(p);
			}
		}
	}
	
	
	
	public void sendShutdown() {
		sendSignals("SHUTDOWN");
	}
	
	
	//========================================================================
	
	// 一个客户端给服务端单独发送消息，One Client ===> Server
	public void sendUpdatedBoardServer(String text) throws Exception {
		sendSignal("OneClientToServer " + text);
	}

	// 给所有客户端发送消息， Server ===> All Client
	public void sendUpdatedBoardClients(String text) {
		GameServer.getInstance().InitRandomPorks();
		sendSignals("ServerToAllClient " + text+","+DomainServer.getInstance().serialize(GameServer.getInstance().getPorks()));
	}
	
	/*
	 * 
	 * 
	 * 
	 * TCP COMMUNICATION / INFORMATION
	 * 
	 * 
	 * 
	 */
	public void sendSignal(String string) throws Exception {
		L.d("sending to server " + string);
		threadTCPClient.out(string);
	}
	
	public void sendSignal(Integer client, String string) throws Exception {
		L.d("sending to player " + client + " " + string);
		players.get(client).out(string);
	}

	public void sendSignals(String string) {
		TCPServerSend threadTCPServerSend = new TCPServerSend(players, string);
		threadTCPServerSend.start();
	}

	public void sendSignals(Vector<String> vector_string) {
		TCPServerSend threadTCPServerSend = new TCPServerSend(players, vector_string);
		threadTCPServerSend.start();
	}

	public void sendSignalsStartGame() {
		TCPServerSend threadTCPServerSend = new TCPServerSend(players);
		threadTCPServerSend.start();
	}

	
	
	
	
	//------------------------------
	public Vector<String> serverTCPGetConnectedPlayersName() {
		Vector<String> result = new Vector<String>(players.size());

		for (int i = 0; i < players.size(); i++) {
			result.add(players.get(i).getName());
		}

		return result;
	}


	public Integer serverTCPGetConnectedPlayersNum() {
		return this.players.size();
	}

	public String serverTCPGetConnectedPlayer(int id) {
		String result = players.get(id).getName();

		return result;
	}


	
	
	
}
