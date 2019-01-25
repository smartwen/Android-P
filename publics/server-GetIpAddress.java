package com.example.serverdemo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Message;

public class GetIpAddress {
	
	private static String IP;
	private static int PORT;
	
	public static String getIP(){
		return IP;
	}
	public static int getPort(){
		return PORT;
	}
	public static void getLocalIpAddress(ServerSocket serverSocket){
		try {
    		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
    			NetworkInterface intf = en.nextElement();
    			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();	enumIpAddr.hasMoreElements();){
    				InetAddress inetAddress = enumIpAddr.nextElement();  				
    				String mIP = inetAddress.getHostAddress().substring(0, 3);  
    				if(mIP.equals("192")){
    					ServerActivity.serverMessage += "请连接IP"+inetAddress.getHostAddress()+":"
    							+ serverSocket.getLocalPort();	
    					IP = inetAddress.getHostAddress();//获取IP
    	    			PORT = serverSocket.getLocalPort();//获取PORT
    	    			Message msg = new Message();
    	    			msg.what = 2;
    	    			ServerActivity.mHandler.sendMessage(msg);
    				}
    			}
    		}
    	}catch (SocketException e){
    		ServerActivity.serverMessage = "获取IP地址异常:" + e.getMessage();//消息换行
    		Message msg = new Message();
    		msg.what = 0;
    		ServerActivity.mHandler.sendMessage(msg);
    	}
    	Message msg = new Message();
    	msg.what = 0;
    	ServerActivity.mHandler.sendMessage(msg);
	}
}
