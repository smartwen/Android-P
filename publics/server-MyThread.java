package com.example.serverdemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.Message;

public class MyThread implements Runnable{
	
	public static ServerSocket serverSocket = null;//服务Socket
	public static Socket mSocketClient = null;//用于和客户端连接的socket
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
		/*创建TCP服务端
			--如果把参数 port 设为 0, 表示由操作系统来为服务器分配一个任意可用的端口
			--如果主机只有一个IP 地址, 那么默认情况下, 服务器程序就与该IP 地址绑定*/
			serverSocket = new ServerSocket(8000);//绑定serverSocket
			SocketAddress address = null;	
			if(!serverSocket.isBound())	{
			/*如果绑定不成功，则将serverSocket绑定至任何可用的IP及空闲PORT*/
				serverSocket.bind(address, 0);
			}
			//获取绑定的IP及PORT
			GetIpAddress.getLocalIpAddress(serverSocket);
			while(ServerActivity.serverRunning){//服务默认关闭
				//方法用于等待客户连接
				if(mSocketClient == null){
					mSocketClient = serverSocket.accept();
					Message clientOnline = new Message();
					clientOnline.what = 0;
					ServerActivity.serverMessage = "Client online";
					ServerActivity.mHandler.sendMessage(clientOnline);
					//接受客户端数据BufferedReader对象
					ReceiveMessage.bufferFromServer = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
					//给客服端发送数据
					SendMessage.bufferToClient = new PrintWriter(mSocketClient.getOutputStream(),true);
				}				
				ReceiveMessage.receiveMessage();
			}
			
		}catch(Exception e){
			Message msg = new Message();
			msg.what = 0;
			ServerActivity.serverMessage = "创建异常:" + e.getMessage() + e.toString();
			ServerActivity.mHandler.sendMessage(msg);
			return;
		}
	}

}
