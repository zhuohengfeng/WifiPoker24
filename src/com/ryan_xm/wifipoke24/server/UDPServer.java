package com.ryan_xm.wifipoke24.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.InvalidParameterException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ryan_xm.wifipoke24.util.L;

public class UDPServer extends Thread {

	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	// 保存当前UDP线程是服务端，还是客户端
	private int mode;
	public static Integer MODE_SERVER = 1;
	public static Integer MODE_CLIENT = 2;
	
	// 组播UDP
	private MulticastSocket multicastSocket;
	
	// 线程运行控制flag
	private Boolean keepRunning;
	
	// 这个handler主要用于给client端更新UI
	private Handler handler;
	
	/** 这里mode=1表示服务端； mode=2表示客户端； handler表示。。。 */
	public UDPServer(int mode, Handler handler) {
		super();

		// 设置线程名称
		setName("UPDServer");

		if (mode != MODE_SERVER && mode != MODE_CLIENT) {
			throw new InvalidParameterException();
		}

		this.handler = handler;
		this.keepRunning = true;
		this.mode = mode;

		// Special socket (catches broadcast) on specified port
		try {
			//L.d("UDPServer now port:"+NetServer.UDP_PORT+", mode:"+mode);
			
			// 创建组播方式的UDP socket
			multicastSocket = new MulticastSocket(NetServer.UDP_PORT);  
            InetAddress address = InetAddress.getByName(NetServer.UDP_IP);  
            multicastSocket.joinGroup(address); 
		} catch (Exception e) {
			L.e("Exception creating socket", e);
			try {
				if(null!=multicastSocket && !multicastSocket.isClosed()){
					multicastSocket.leaveGroup(InetAddress.getByName(NetServer.UDP_IP));
					multicastSocket.close();
				}
			} catch (Exception e1) {
				L.e("Exception creating socket", e1);
			}
		}
	}
	
	
	public void run() {
		try {
			byte[] buf = new byte[1024];

			//Listen on socket to receive messages 
			while (keepRunning && !multicastSocket.isClosed() && multicastSocket != null) {

				L.d("Inside while");

				// Wait for a new packet 
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				// 这里会阻塞住，一直等待新的报文传递过来！！！
				multicastSocket.receive(packet);
				
				L.d("Datagram received");

				InetAddress remoteIP = packet.getAddress();

				// 这里PING广播是client发送的，但是他也会发送给自己，所以这里要加一个判断
				if ((remoteIP.getHostAddress().toString()).equals(NetServer.getInstance().getLocalAddress().getHostAddress().toString()))
					continue;

				// 如果不是自己发给自己的，则继续处理
				String content = new String(packet.getData(), 0, packet.getLength());
				L.d("Content: " + content);

				if (mode == MODE_SERVER) {
					// SERVER---服务端在创建UDP线程之后，会一直等待客户端发送"PING"消息过来
					
					L.d("Mode server entered");

					// Send an answer to the client
					// 如果收到了，就给接收的客户端发送 ： 当前游戏的名称 | Tcp端口号 
					sendIP(remoteIP, DomainServer.getInstance().getServerName() + "|" + NetServer.TCP_PORT);
					
					L.d("Sent answer to client");

				} else if (mode == MODE_CLIENT) {

					// CLIENT---如果是客户端，不会接收PING消息，所以这里接收的是服务端发送过来的： 当前游戏的名称 | 端口号 ，并获取服务端的IP地址
					// 可以把这些更新到JoinGameWaiting这个界面
					L.d("Mode client entered");

					sendUIServer(content.split("\\|")[0], remoteIP, content.split("\\|")[1]);
				}

				L.d("Finished work");
			}

		} catch (Exception e) {
			e.printStackTrace();
			L.d("Exception e:"+e);
		}

	}

	// 断开UDP线程， 离开组播UDP
	public void close() {
		this.keepRunning = false;
		//socket.close();
		try {
			if(null!=multicastSocket && !multicastSocket.isClosed()){
				multicastSocket.leaveGroup(InetAddress.getByName(NetServer.UDP_IP));
				multicastSocket.close();
			}
		} catch (Exception e1) {
			L.e("Exception creating socket", e1);
		}
	}
	
	/** 向组播的IP地址和端口， 发送消息， 这里由于是给组播IP发送，所以是广播的， 所有socket都能接受到 */
	public void sendBroadcast(String data) {
		try {
			InetAddress local = InetAddress.getByName(NetServer.UDP_IP); 
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), local, NetServer.UDP_PORT);
			multicastSocket.send(packet);
			L.d("Datagram sent  data="+data);
		} catch (Exception e) {
			L.e("Exception during sendBroadcast", e);
		}
	}

	/** 给组播内某个具体的IP发送消息， 这里就是服务端给刚刚连接上的客户端发送“新游戏”的消息 */
	public void sendIP(InetAddress ip, String data) {
		try {
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), ip, NetServer.UDP_PORT);
			multicastSocket.send(packet);
			L.d("Datagram sent");
		} catch (Exception e) {
			L.e("Exception during sendIP", e);
		}
	}

	// 客户端收到服务端发过来的 游戏名称， 服务端IP， 服务端port 之后更新joinGameWaiting界面
	private void sendUIServer(String name, InetAddress remoteIP, String port) {
		L.d("sendUIServer name="+name+", getHostAddress="+remoteIP.getHostAddress()+", port="+port);
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("NAME", name);
		data.putString("IP", remoteIP.getHostAddress());
		data.putInt("PORT", Integer.valueOf(port));
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	
	
	
	
	
	
	
}
