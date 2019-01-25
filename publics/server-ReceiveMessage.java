package com.example.serverdemo;

import java.io.BufferedReader;

import android.os.Message;
import android.util.Log;

public class ReceiveMessage {
	public static BufferedReader bufferFromServer	= null;
	private static String content;
	public static void receiveMessage(){
			try{
				if((content = bufferFromServer.readLine()) != null){
					ServerActivity.clientMessage = content;

					if(content.equals("Reboot")){
						bufferFromServer.close();
						SendMessage.bufferToClient.close();
						MyThread.mSocketClient.close();
						MyThread.mSocketClient = null;
					}							
					Message receiveMeg = new Message();
					receiveMeg.what = 1;
					ServerActivity.mHandler.sendMessage(receiveMeg);
				}
			}catch(Exception e){
				ServerActivity.serverMessage = "接收异常::" + e.getMessage() ;
				Message exceptionMsg = new Message();
				exceptionMsg .what = 0;
				ServerActivity.mHandler.sendMessage(exceptionMsg);
				return;
			}
	}		
}
