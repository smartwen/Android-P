package com.example.clientdemo;

import java.io.BufferedReader;

import android.os.Message;
import android.util.Log;

import static com.example.clientdemo.MainActivity.messageHandler;

/**
 * Created by Administrator on 2018/10/26.
 */

public class ReceiveMessage {
    private static String content = null;
    public static BufferedReader bufferFromServer = null;
    private static String msgHead = "";//匹配信息头标

    public ReceiveMessage() {

    }

    public static void receiveMessage() {
        // TODO Auto-generated method stub
        while (MainActivity.isConnecting) {
            try {
                //不管是客户端还是服务端在通过Socket读取数据的时候，在你的数据传输完成后，
                // 发送一个结束标记(比如:end)，这样在你的 while循环里面判断一下数据是否已经传输完毕，
                // 完毕后跳出循环，否则由于你的程序就会一直阻塞在 readLine()这里，
                // 因为你的 socket还没有断开，会一直等待你写数据。

                while ((content = bufferFromServer.readLine()) != null) {
                    MainActivity.serverMessage = content.substring(0,content.length()-4);
                    //  MainActivity.clientMessage = "Server online";

                    if (content.equals("Reboot")) {
                        bufferFromServer.close();
                        SendMessage.bufferToServer.close();
                        MyThread.mSocketClient.close();
                        MyThread.mSocketClient = null;
                    }
                    Log.d("message", "------");

                    Message serverMessageReceive = new Message();
                    serverMessageReceive.what = 0;
                    messageHandler.sendMessage(serverMessageReceive);
                }
            } catch (Exception e) {
                MainActivity.clientMessage = "接收异常:" + e.getMessage();//消息换行
                Message serverMessageReceive = new Message();
                serverMessageReceive.what = 1;
                messageHandler.sendMessage(serverMessageReceive);
            }
        }
    }
}
