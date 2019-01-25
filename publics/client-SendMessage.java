package com.example.clientdemo;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import static com.example.clientdemo.MyThread.mSocketClient;

/**
 * Created by Administrator on 2018/10/26.
 */

public class SendMessage implements Runnable{
    public static PrintWriter bufferToServer = null;
    private static String msgHead;//摘取命令标头
    public static void sendMessage(String message,Context context){


    }
    //判断客户端是否在线
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
        if (mSocketClient == null){
            return;
        }
        String message = MainActivity.sendText;
        //连接状态标识位 false  客户端Socket存在
        if (MainActivity.isConnecting && mSocketClient!=null)
        {
            try {
                bufferToServer.print(message+"\r\n");//将编辑框内容发送给服务器
                bufferToServer.flush();
                Log.d("TAG_SEND","输入的值："+MainActivity.sendText);
                MainActivity.clientMessage = message;
                Message msg = new Message();
                msg.what = 1;
                MainActivity.messageHandler.sendMessage(msg);
            }catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(context,"发送消息失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

