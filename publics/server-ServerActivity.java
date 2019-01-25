package com.example.serverdemo;

import java.io.IOException;
import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class ServerActivity extends Activity implements OnClickListener{
	
	private static TextView info_ip_and_port;
	private static TextView dialog_info;
	private static ScrollView mScrollView; 
	private static LinearLayout dialog;
	private static TextView title_info;
	
	private Button creat,send,clear;
	private EditText chat_info;	
	private Thread mThreadServer;//服务线程
    private Handler viewHandler = new Handler();
	public static String msgText = "";
   
    
	public static  String clientMessage = "";//客户端发送的消息
	public static  String serverMessage = "";	//服务端发送的消息
	public static boolean serverRunning = false;//服务状态标志位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setContentView(R.layout.activity_server);
		initWidgets();
      /*事件处理*/
        creat.setOnClickListener(this);
        send.setOnClickListener(this);
        clear.setOnClickListener(this);
        chat_info.setOnClickListener(this);
       
    }

	/**
	 * 控件获取
	 */
	private void initWidgets(){
		info_ip_and_port = (TextView)findViewById(R.id.info_ip_and_port);
		dialog_info = (TextView)findViewById(R.id.dialog_info);
		mScrollView = (ScrollView)findViewById(R.id.scroll);
		dialog = (LinearLayout)findViewById(R.id.dialog);
		creat = (Button)findViewById(R.id.creatServer);
		send = (Button)findViewById(R.id.send);
		clear = (Button)findViewById(R.id.clear);
		chat_info = (EditText)findViewById(R.id.chat_info);
		title_info = (TextView)findViewById(R.id.title_info);
		chat_info.setMovementMethod(ScrollingMovementMethod.getInstance());
		chat_info.setSelection(chat_info.getText().length(), chat_info.getText().length());
	}

	//创建服务
    public void creatServer(){
    	if(serverRunning){
    		serverRunning = false;
    		try{
    			if(MyThread.serverSocket!=null){
					MyThread.serverSocket.close();
					MyThread.serverSocket = null;
				}
    			if(MyThread.mSocketClient!=null){
					MyThread.mSocketClient.close();
					MyThread.mSocketClient = null;
				}
			}catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mThreadServer.interrupt();
			creat.setBackgroundResource(R.drawable.creat);
			title_info.setText("欢迎使用");
			dialog_info.setText("信息:\n");
    	}else{
    		serverRunning = true;
    		MyThread creatServer = new MyThread();
    		mThreadServer = new Thread(creatServer);//创建服务线程
    		mThreadServer.start();
    		creat.setBackgroundResource(R.drawable.creat_off);
    	}
    }    
    //接收线程传递来的消息更新UI
    public static Handler mHandler = new Handler(){
    	
    	public void handleMessage(Message msg)										
    	{											
    		super.handleMessage(msg);			
    		if(msg.what == 0){
    			if(serverMessage.equals("Client online")){
						title_info.setText("客户端已连接");
    			}else{
    				dialog_info.append("Server: "+ serverMessage + "\n");	// 刷新
        			scroll2Bottom(mScrollView,dialog);
    			}   			
    		}else if(msg.what == 1){
    			dialog_info.append("Client: "+ clientMessage + "\n");
    			scroll2Bottom(mScrollView,dialog);
    		}else if(msg.what == 2){
    			info_ip_and_port.setText(GetIpAddress.getIP() + ":" + GetIpAddress.getPort());
    		}
    	}
    };    
    // TextView自带滚动条、自动滚到最底
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


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.creatServer:
			creatServer();
			break;
		case R.id.send:
			msgText = chat_info.getText().toString();//取得编辑框中我们输入的内容
			if(msgText.equals("")){
				Toast.makeText(this, "发送内容为空，请重新输入", Toast.LENGTH_SHORT).show();
			}else{
				if(SendMessage.isConnected(MyThread.mSocketClient)){
					//SendMessage.sendMessage(msgText, ServerActivity.this);
					SendMessage sendMessage = new SendMessage();
					Thread sendThread = new Thread(sendMessage);//创建发送消息线程
					sendThread.start();
					chat_info.setText("");
				}else{
					Toast.makeText(this, "连接已断开，请重新连接", Toast.LENGTH_SHORT).show();
					title_info.setText("客户端已断开");
					try {
						if(MyThread.mSocketClient != null){
							ReceiveMessage.bufferFromServer.close();
							MyThread.mSocketClient.close();
							SendMessage.bufferToClient.close();
							MyThread.mSocketClient = null;
						}						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}
			
			break;
		case R.id.clear:
			dialog_info.setText("");//清空输入
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
		default :
				break;
		}
	} 	
}
