package com.example.clientdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //定义属性
    private String serverIP = null;//服务器IP地址
    private int serverPort = 0;//服务器PORT
    private Thread mThreadClient = null;//创建Socet连接的子线程

    private Handler viewHandler = new Handler();//实现TextView拖动的功能
    public static int serverNum = 0;
    //定义控件
    private static Button connect1;
    private Button clear;
    private Button send;
    private static TextView dialog_info;
    private EditText chat_info,ipAddress1;
    private static ScrollView mScrollView;
    private static LinearLayout dialog;
    private static TextView title_info;

    //定义公共属性
    public static boolean isConnecting = false;//连接状态标识位
    public static String clientMessage = "";//客户端发送的消息
    public static String serverMessage = "";//客户端接收的消息
    public static String  clientName ="Client";//客户端用户名
    public static String serverName = "Server";//服务端用户名
    public static String sendText = null;//发送的字符串
    //记录上次输入的IP
    private SharedPreferences ipLogged_1 = null;
    private SharedPreferences.Editor ipLogEditor_1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setContentView(R.layout.activity_main);
        //上次IP记录初始化
        ipLogged_1 = getSharedPreferences("lastIP_1",MODE_PRIVATE);
        ipLogEditor_1 = ipLogged_1.edit();
        initWidgets();
        widgetListener();
        //获取用户名及密码
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();//得到intent所附带的额外数据
        //clientName = bundle.getString("userName");

    }
    //获取控件
    private void initWidgets(){
        connect1 = (Button)findViewById(R.id.connect1);
        clear = (Button)findViewById(R.id.clear);
        send = (Button)findViewById(R.id.send);
        dialog_info = (TextView)findViewById(R.id.dialog_info);
        chat_info = (EditText)findViewById(R.id.chat_info);
        ipAddress1 = (EditText)findViewById(R.id.ipAddress1);
        mScrollView = (ScrollView) findViewById(R.id.scroll);
        dialog = (LinearLayout)findViewById(R.id.dialog);
        title_info = (TextView)findViewById(R.id.title_info);
    }
    //控件监听
    private  void widgetListener(){
        chat_info.setMovementMethod(ScrollingMovementMethod.getInstance());//EditText滚动条的实现
        chat_info.setSelection(chat_info.getText().length(), chat_info.getText().length());
        connect1.setOnClickListener(this);
        send.setOnClickListener(this);
        clear.setOnClickListener(this);
        chat_info.setOnClickListener(this);
    }
    //创建连接
    private void connectServer(String IP,int Port,int Num){
        serverNum = Num;
        if(isConnecting){//已连接进行关闭
            SendMessage.sendMessage("Reboot\r\n", MainActivity.this);
            isConnecting = false;
            try {
                if(MyThread.mSocketClient!=null){
                    MyThread.mSocketClient.close();
                    MyThread.mSocketClient = null;
                    SendMessage.bufferToServer.close();
                    SendMessage.bufferToServer = null;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mThreadClient.interrupt();
            if(serverNum == 1){
                connect1.setText("连接");
                connect1.setBackgroundResource(R.drawable.connect1);
            }
            dialog_info.setText("信息:\n");
            title_info.setText("欢迎使用");
            serverNum = 0;
        }else{
            MyThread myThread = new MyThread(IP,Port,MainActivity.this);
            mThreadClient = new Thread(myThread);
            mThreadClient.start();
        }
    }

    //将收发的信息显示到文本框中
    public static Handler messageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what == 1){
                if(clientMessage.equals("Server online")){
                    title_info.setText("『"+ serverName +"』"+"已连接");
                }else{
                    Log.d("message","客户端发送消息");
                    dialog_info.append(clientName + ": "+clientMessage + "\n");	// 刷新
                    scroll2Bottom(mScrollView,dialog);
                }
            }else if(msg.what == 0){//服务器发送消息过来
                dialog_info.append(serverName +": "+ serverMessage + "\n");	// 刷新
                scroll2Bottom(mScrollView,dialog);
            }
        }
    };

    //根据是否连接成功来更改按键的显示状态
    public static Handler connectSuccess = new Handler(){

        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 0){//与服务器连接成功
                if(serverNum == 1){
                    connect1.setText("断开");
                    connect1.setBackgroundResource(R.drawable.connect2);
                }
            }else if(msg.what == 1){//与服务器断开连接
                if(serverNum == 1){
                    connect1.setText("连接");
                    connect1.setBackgroundResource(R.drawable.connect1);
                }
                serverNum = 0;
            }
        }
    };

    //按键监听
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.connect1:
                //与服务器连接
                    if(ipAddress1.getText().toString().equals("")){
                        Toast.makeText(this, "IP地址为空,程序将使用上次IP", Toast.LENGTH_SHORT).show();
                        serverIP = ipLogged_1.getString("ip", "none");
                        ipAddress1.setText(serverIP);
                    }else{
                        serverIP = ipAddress1.getText().toString();
                        ipLogEditor_1.clear().commit();
                        ipLogEditor_1.putString("ip", serverIP);
                        ipLogEditor_1.commit();
                    }
                    serverPort = 8000;
                    connectServer(serverIP ,serverPort,1);
                break;
            
            case R.id.send:
                sendText = chat_info.getText().toString();//取得编辑框中我们输入的内容			
                if(sendText != null){
                    if(SendMessage.isConnected(MyThread.mSocketClient)){
                       // SendMessage.sendMessage(sendText,MainActivity.this);
                        SendMessage sendMessage = new SendMessage();
                        Thread sendThread = new Thread(sendMessage);//创建发送消息线程
                        sendThread.start();
                        chat_info.setText("");
                    }else{
                        Toast.makeText(MainActivity.this, "服务端已断开,请打开服务端", Toast.LENGTH_SHORT).show();
                        if(serverNum == 1){
                            connect1.setText("连接");
                            connect1.setBackgroundResource(R.drawable.connect1);
                            isConnecting = false;
                            serverNum = 0;
                        }
                        title_info.setText("『"+ serverName +"』"+"已断开");
                    }
                }else{
                    Toast.makeText(MainActivity.this, "发送内容为空，请重新输入！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear:
                dialog_info.setText("");
                break;
            case R.id.chat_info:
                viewHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //将ScrollView滚动到底  
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 100);
                break;
            default:
                break;
        }
    }

    //对话文本框自动滚动到最新信息
    public static void scroll2Bottom(final ScrollView scroll, final View inner) {
        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (scroll == null || inner == null) {
                    return;
                }
                // 内层高度超过外层
                int offset = inner.getMeasuredHeight()
                        - scroll.getMeasuredHeight();
                if (offset < 0) {
                    System.out.println("定位...");
                    offset = 0;
                }
                scroll.scrollTo(0, offset);
            }
        });
    }

}
