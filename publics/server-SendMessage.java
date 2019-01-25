package com.example.serverdemo;

import java.io.PrintWriter;
import java.net.Socket;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

public class SendMessage implements Runnable{
	public static PrintWriter bufferToClient = null;
	public static void sendMessage(String message,Context context){

	}
	public static boolean isConnected(Socket socket){
        try{
        	socket.sendUrgentData(0xFF);
            return true;
        }catch(Exception e){
            return false;
        }
	}

	@Override
	public void run() {
		String message = ServerActivity.msgText ;
		if (ServerActivity.serverRunning && MyThread.mSocketClient!=null ){

			try {
				bufferToClient.println(message + "/r/n");//发送给客户端
				Log.d("SendMessage", "客户端你好啊  快来玩耍吧");
				bufferToClient.flush();//flush()表示强制将缓冲区中的数据发送出去,不必等到缓冲区满
				Message msg = new Message();
				msg.what = 0;
				ServerActivity.serverMessage = message;
				ServerActivity.mHandler.sendMessage(msg);

			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
				//Toast.makeText(context, "发送异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}

		}
	}
}
