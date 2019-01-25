package com.oppotest.commtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;
import gov.nist.javax.sip.header.CSeq;
import android.widget.TextView;
import android.widget.Toast;

import com.oppotest.commtools.util.CommonMethod;
import com.oppotest.commtools.util.FillMethods;
import com.oppotest.commtools.util.FillUtils;
import com.oppotest.commtools.util.searchAttribution;
import com.oppotest.commtools.util.searchAttribution.NumberAttributionInfo;

@SuppressLint("ResourceAsColor")
public class CallLogActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	public Button bt_fillCallsInsert, bt_callsSelectContacts, bt_fillCallsStart;
	public TextView tx_blacklist, tx_detail;
	CheckBox cb_unknow, cb_cipher, cb_longLocation, cb_longTime, cb_86, cb_yellow;

	RadioGroup group, markNumberGroup;
	RadioButton rb_random, rb_manual, rb_contacts, rd_Harassingcall, rd_SuspectedFraud, rd_Courierdelivery,
			rd_mediation;

	EditText inputEdit, numEdit, specialCallogEdit;
	CheckBox cb_missed, cb_received, cb_dialed;
	CheckBox cb_blacklist, cb_negative, cb_region, cb_ringOnce, cb_all, cb_allStrangers;
	EditText ed_holdUpNumber;// 拦截来电类型 数量

	AlertDialog dlg;
	TextView updateMsg;

	public FillMethods methods;

	private static final int RANDOM = 1;
	private static final int MANUAL = 2;
	private static final int CONTACTS = 3;
	public int numSource = 1;
	public int markType = 0;

	private Context mContext;
	private ArrayList<String> numList;
	private String[] contactsArray;
	private static Boolean[] isSelected;

	// 线程同步锁

	// 消息指令
	private static final int PROCESS_UPDATE = 0x31;
	private static final int PROCESS_DISMISS = 0x32;
	private static final int MSG_CONTACT_DIALOG = 0x33;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout1);

		initLayoutAndWidgets();
		initData();
	}

	// 初始化布局和控件
	private void initLayoutAndWidgets() {
		bt_fillCallsInsert = (Button) findViewById(R.id.bt_insertCallLog);
		bt_fillCallsInsert.setOnClickListener(this);

		cb_unknow = (CheckBox) findViewById(R.id.cb_unkonw);
		cb_cipher = (CheckBox) findViewById(R.id.cb_cipher);
		cb_longLocation = (CheckBox) findViewById(R.id.cb_longLocation);
		cb_longTime = (CheckBox) findViewById(R.id.cb_longTime);
		cb_86 = (CheckBox) findViewById(R.id.cb_86);
		// cb_yellow = (CheckBox) findViewById(R.id.cb_YellowPage);
		specialCallogEdit = (EditText) findViewById(R.id.ed_insertSpecialNumber);
		specialCallogEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

		group = (RadioGroup) findViewById(R.id.radionGropuSourceCallsType);
		group.setOnCheckedChangeListener(this);
		rb_random = (RadioButton) findViewById(R.id.rd_callsRandom);
		rb_manual = (RadioButton) findViewById(R.id.rd_callsCustm);
		rb_contacts = (RadioButton) findViewById(R.id.rd_callsContacts);

		inputEdit = (EditText) findViewById(R.id.ed_callsRandomNumber);
		inputEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

		bt_callsSelectContacts = (Button) findViewById(R.id.bt_fillCallsSelectContacts);
		bt_callsSelectContacts.setOnClickListener(this);
		bt_callsSelectContacts.setVisibility(View.GONE);

		cb_missed = (CheckBox) findViewById(R.id.cb_missedCall);
		cb_received = (CheckBox) findViewById(R.id.cb_receivedCall);
		cb_dialed = (CheckBox) findViewById(R.id.cb_dialedCall);

		numEdit = (EditText) findViewById(R.id.ed_callsNumber);
		numEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

		bt_fillCallsStart = (Button) findViewById(R.id.bt_fillCalssStart);
		bt_fillCallsStart.setOnClickListener(this);

		markNumberGroup = (RadioGroup) findViewById(R.id.radionGropuMarkNumType);
		markNumberGroup.setOnCheckedChangeListener(this);
		rd_Harassingcall = (RadioButton) findViewById(R.id.rd_Harassingcall);
		rd_SuspectedFraud = (RadioButton) findViewById(R.id.rd_SuspectedFraud);
		rd_Courierdelivery = (RadioButton) findViewById(R.id.rd_Courierdelivery);
		rd_mediation = (RadioButton) findViewById(R.id.rd_mediation);

		cb_blacklist = (CheckBox) findViewById(R.id.cb_blacklist);
		cb_negative = (CheckBox) findViewById(R.id.cb_negative);
		cb_region = (CheckBox) findViewById(R.id.cb_region);
		cb_ringOnce = (CheckBox) findViewById(R.id.cb_ringOnce);
		cb_allStrangers = (CheckBox) findViewById(R.id.cb_allStrangers);
		cb_all = (CheckBox) findViewById(R.id.cb_all);
		ed_holdUpNumber = (EditText) findViewById(R.id.ed_holdUpNumber);
	}

	/** 初始化数据 */
	private void initData() {
		mContext = CallLogActivity.this;
		methods = new FillMethods(getApplicationContext());
		contactsArray = new String[0];
		isSelected = new Boolean[0];
	}

	/** 按键点击 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_insertCallLog: // 插入通话记录
			// 填充特殊通话记录
			fillSpecialCallLog();

			break;
		case R.id.bt_fillCallsSelectContacts: // 选择联系人
			selectContacts();
			break;
		case R.id.bt_fillCalssStart: // 开始填充

			// 批量填充通话记录
			fillBatchCallLogs();
			break;
		}
	}

	private void selectContacts() {
		CommonMethod.showCustomProcessingBar(CallLogActivity.this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> contactsList = methods.getAllContactNumbers();
				contactsArray = contactsList.toArray(new String[contactsList.size()]);// （从第一个到最后一个元素）返回包含此列表中所有元素的数组
				CommonMethod.dismissCustomProcessingBar();
				mHandler.sendEmptyMessage(MSG_CONTACT_DIALOG);
			}
		}).start();

	}

	@Override // 单选框
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (rb_random.getId() == checkedId) {
			// 随机生成
			inputEdit.setVisibility(View.VISIBLE);
			inputEdit.setHint(R.string.tx_phoneNumber);
			bt_callsSelectContacts.setVisibility(View.GONE);// 选择联系人号码填充 按钮
			numSource = 1;
		} else if (rb_manual.getId() == checkedId) {
			// 手动输入
			inputEdit.setVisibility(View.VISIBLE);// invisible： 不显示，但占据着布局的位置；
			inputEdit.setHint(R.string.tx_manual);
			bt_callsSelectContacts.setVisibility(View.GONE);
			numSource = 2;
		} else if (rb_contacts.getId() == checkedId) {
			// 选择联系人号码
			inputEdit.setVisibility(View.GONE);// gone：不显示，也不占布局的位置；
			bt_callsSelectContacts.setVisibility(View.VISIBLE);
			numSource = 3;
		} else if (rd_Harassingcall.getId() == checkedId) {// 选择骚扰电话
			markType = 1;
		} else if (rd_SuspectedFraud.getId() == checkedId) {// 选择疑似诈骗
			markType = 2;
		} else if (rd_Courierdelivery.getId() == checkedId) {// 单选快递外卖
			markType = 3;
		} else if (rd_mediation.getId() == checkedId) {// 选择房产中介
			markType = 4;
		}
	}

	/** 消息处理 */
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {// 根据收到的消息的what类型处理
			case PROCESS_UPDATE: // 更新进度
				int per = msg.arg1;
				if (updateMsg != null)
					updateMsg.setText("已填充... " + per + "个。");

				break;
			case PROCESS_DISMISS:
				if (dlg != null)
					dlg.dismiss();

				Toast.makeText(mContext, "填充完成！", Toast.LENGTH_SHORT).show();
				break;
			case MSG_CONTACT_DIALOG:
				showContactsDialog(contactsArray);
				break;
			}
			return false;
		}
	});

	/** 获取号码输入框内容 */
	public String getInputEditText() {
		if (inputEdit.getText().length() == 0) {
			return null;
		} else {
			return inputEdit.getText().toString();
		}
	}

	/** 获取数量输入框内容 */
	public String getNumEditText() {
		if (numEdit.getText().length() == 0) {
			return null;
		} else {
			return numEdit.getText().toString();
		}
	}

	/** 获取特殊通话记录输入框内容 */
	public String getSpecialCallogEditText() {
		if (specialCallogEdit.getText().length() == 0) {
			return null;
		} else {
			return specialCallogEdit.getText().toString();
		}
	}

	/** 获取拦截来电记录插入数量 */
	public String getholdUpNumberText() {
		if (ed_holdUpNumber.getText().length() == 0) {
			return null;
		} else {
			return ed_holdUpNumber.getText().toString();
		}
	}

	public class CommonFileRunnable implements Runnable {

		@Override
		public void run() {

			// TODO Auto-generated method stub
			int tag = 1;
			long date = System.currentTimeMillis();
			long baseNumber = 18600000001L;
			String phoneNumber = String.valueOf(baseNumber);
			int ring_time = 20;
			int type = 3;
			long duration = 10;
			String numberAttString;
			for (int i = 0; i < Integer.parseInt(getSpecialCallogEditText()); i++) {
				// 未知
				if (cb_unknow.isChecked()) {
					phoneNumber = "-1";
					ring_time = 30;
					numberAttString = "短号码";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
				}

				// 私人
				if (cb_cipher.isChecked()) {
					phoneNumber = "-2";
					ring_time = 40;
					type = 3;
					duration = 30;
					numberAttString = "短号码";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
				}

				// 长归属地
				if (cb_longLocation.isChecked()) {
					phoneNumber = "099713724511368";
					ring_time = 40;
					duration = 30;
					type = 3;
					numberAttString = "新疆阿克苏/阿拉尔市";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
				}

				// 长通话时间
				if (cb_longTime.isChecked()) {
					phoneNumber = "13724518888";
					ring_time = 40;
					duration = 36776;
					type = 1;
					numberAttString = "广东东莞";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
				} // +86的未接来电
				if (cb_86.isChecked()) {
					phoneNumber = "+8613724518888";
					ring_time = 40;
					duration = 30;
					type = 3;
					numberAttString = "广东东莞";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
				}

				// 增加插入标记号码来电，可选择标记类型，如，骚扰电话、疑似诈骗、快递外卖、房产中介
				switch (markType) {
				case 1:// 骚扰电话
					phoneNumber = "031186880221";// 
					ring_time = 40;
					duration = 20;
					type = 3;
					numberAttString = "广东深圳";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					break;
				case 2:// 疑似诈骗
					phoneNumber = "13022142605";// 13371571337 13439223684
												// 85251679747 10692588300012
					ring_time = 40;
					duration = 30;
					type = 3;
					numberAttString = "上海";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					break;
				case 3:// 快递外卖
					phoneNumber = "18128849745";
					ring_time = 40;
					duration = 30;
					type = 3;
					numberAttString = "广东深圳";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					break;
				case 4:// 房产中介
					phoneNumber = "18962662225";// 051257509883 13511629011
					ring_time = 40;
					duration = 30;
					type = 3;
					numberAttString = "福建泉州";
					Log.d("tx", numberAttString);
					methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					break;
				default:
					break;
				}

			}
			// 增加插入拦截来电记录
			if (getholdUpNumberText()!= null) {
				for (int i = 0; i < Integer.parseInt(getholdUpNumberText()); i++) {
					if (cb_blacklist.isChecked()) {
						// 黑名单号码拦截类型 填充10086（显示拦截电话） 一个号码通话记录 将该号码改为黑名单
						type = 27;
						phoneNumber = "17612345678";
						numberAttString = "广东深圳";
						Log.d("tx", numberAttString);
						methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					}

					if (cb_negative.isChecked()) {// 负面标记拦截类型
						int[] calogtype = new int[] { 50, 51, 52, 53 };
						type = calogtype[getNum(0, calogtype.length-1)];
						if (type == 50) {
							phoneNumber = "13439223684";
						} else if (type == 51) {
							phoneNumber = "075561264621";
						} else if (type == 52) {
							phoneNumber = "079182260879";
						} else if (type == 53) {
							phoneNumber = "13511629011";
						}
						numberAttString = "广东深圳";
						Log.d("tx", numberAttString);
						methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					}

					if (cb_region.isChecked()) {// 地区拦截类型
						type = 24;
						phoneNumber = "18012345678";
						numberAttString = "江苏扬州";
						Log.d("tx", numberAttString);
						methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);

					}

					if (cb_ringOnce.isChecked()) {// 响一声来电拦截类型
						phoneNumber = "13724516666";
						ring_time = 1;
						duration = 0;
						type = 20;
						numberAttString = "广东东莞";
						Log.d("tx", numberAttString);
						methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);
					}
					if (cb_all.isChecked()) {// 所有号码拦截类型
						type = 26;
						phoneNumber = "076512345678";
						numberAttString = "广东深圳";
						Log.d("tx", numberAttString);
						methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);

					}
					if (cb_allStrangers.isChecked()) {// 所有陌生号码拦截类型
						type = 25;
						phoneNumber = "20191314520";
						numberAttString = "广东深圳";
						Log.d("tx", numberAttString);
						methods.fillCallLogs(tag, date, duration, phoneNumber, type, ring_time, numberAttString);

					}
				}
			}

			mHandler.sendEmptyMessage(PROCESS_DISMISS);
		}

	}

	/**
	 * 填充特殊通话记录
	 */
	private void fillSpecialCallLog() {
		String specialCallogEditText = getSpecialCallogEditText();
		if (specialCallogEditText == null) {
			Toast.makeText(mContext, "特殊通话记录数量输入框为空，请输入！", Toast.LENGTH_SHORT).show();
			return;
		}

		String holdUpNumberEditText = getholdUpNumberText();
		if (holdUpNumberEditText == null && cb_blacklist.isChecked()) {
			Toast.makeText(mContext, "拦截来电数量输入框为空，请输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		new Thread(new CommonFileRunnable()).start();

	}

	/** 批量填充 */
	public void fillBatchCallLogs() {
		String inputText = getInputEditText();
		if (inputText == null && numSource != CONTACTS) {
			Toast.makeText(mContext, "输入框为空，请填空！", Toast.LENGTH_SHORT).show();
			return;
		}
		String numText = getNumEditText();
		if (numText == null) {
			Toast.makeText(mContext, "数量输入框为空，请输入！", Toast.LENGTH_SHORT).show();
			return;
		}

		showCustomProcessingBar();

		numList = new ArrayList<String>();
		switch (numSource) {
		case RANDOM:// 随机生成
			int random_num = Integer.valueOf(inputText);
			// int random = (int) (Math.random() * 100);
			for (int i = 0; i < random_num; i++) {
				numList.add(getTel());
			}
			break;
		case MANUAL:// 手动输入
			numList.add(inputText);
			break;
		case CONTACTS:// 电话本联系人
			if (contactsArray != null) {
				for (int i = 0; i < contactsArray.length; i++) {
					if (isSelected[i])
						try {
							numList.add(contactsArray[i].split("\n")[1]);
						} catch (Exception e) {
						}
				}
			}
			break;
		}

		if (numList.size() <= 0) {
			Toast.makeText(mContext, "没有选择需填充的联系人号码！", Toast.LENGTH_SHORT).show();
			return;
		}

		ArrayList<Integer> typeList = new ArrayList<Integer>();
		if (cb_received.isChecked())
			typeList.add(1);// 已接 public static final int INCOMING_TYPE = 1;
		if (cb_dialed.isChecked())// 已拨 public static final int OUTGOING_TYPE =
									// 2;
			typeList.add(2);
		if (cb_missed.isChecked())// 未接 public static final int MISSED_TYPE = 3;
			typeList.add(3);
		if (typeList.size() <= 0) {
			typeList.add(1);
			typeList.add(2);
			typeList.add(3);
		}

		// 开始执行
		new Thread(new FillRunnable(numList, Integer.valueOf(numText), typeList.toArray(new Integer[typeList.size()])))
				.start();

	}

	/** 显示联系人选择框 */
	private void showContactsDialog(String[] contacts) {
		final AlertDialog dlg = new AlertDialog.Builder(CallLogActivity.this).create();
		dlg.show();
		dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		final Window window = dlg.getWindow();
		window.setContentView(R.layout.dialog_get_contacts);// 加载电话联系人布局
		window.setBackgroundDrawableResource(android.R.color.background_light);

		ListView listView = (ListView) window.findViewById(R.id.contacts_list);
		isSelected = new Boolean[contacts.length];
		for (int i = 0; i < contacts.length; i++) {
			isSelected[i] = false;// 联系人默认不选中
		}
		final CallLogAdapter adapter = new CallLogAdapter(mContext, contacts);
		listView.setAdapter(adapter);// 通过适配器 listview和数据建立关联
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int item, long arg3) {// 通过item参数判断用户点击的是哪一个子项
				isSelected[item] = !isSelected[item];// 复选框改变 为选中
				adapter.notifyDataSetChanged();
			}
		});
		Button sureBtn = (Button) window.findViewById(R.id.sureBtn);
		sureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		Button cancelBtn = (Button) window.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}

	/** 填充Runnable */
	public class FillRunnable implements Runnable {
		private NumberAttributionInfo mNumberAttributionInfo;
		private String mNumberAttribution;
		private ArrayList<String> numList;
		private int num;
		private Integer[] type;

		public FillRunnable(ArrayList<String> list, int fillNum, Integer[] fillType) {
			numList = list;
			num = fillNum;
			type = fillType;
			mNumberAttributionInfo = new NumberAttributionInfo();
		}

		@Override
		public void run() {
			int tag = 1;
			int count = 0;
			Message msg;
			for (String number : numList) {
				for (int i = 1; i <= num; i++) {
					HashMap<String, NumberAttributionInfo> map = searchAttribution.queryNumberAttribution(number,
							mContext);
					// 获取地址信息
					// mNumberAttributionInfo = map.get(number+ "");
					// mNumberAttribution =
					// mNumberAttributionInfo.getLocation();
					methods.fillCallLogs(tag, System.currentTimeMillis(), (int) (Math.random() * 50), number,
							type[(int) (Math.random() * type.length)], (int) (Math.random() * 100), "");
					msg = new Message();
					msg.what = PROCESS_UPDATE;// msg.what = 0是给成员变量what赋值
					msg.arg1 = ++count;// arg1 用于携带int整数型数据
					mHandler.sendMessage(msg);
				}
			}
			// 填充完成发送完成消息
			mHandler.sendEmptyMessage(PROCESS_DISMISS);
		}
	}

	/** 联系人listView的Adapter */
	@SuppressLint("ViewHolder")
	public static class CallLogAdapter extends BaseAdapter {

		private String[] opItems;
		private Context context;

		public CallLogAdapter(Context context, String[] options) {// 传进来一个数组
			this.opItems = options;
			this.context = context;

		}

		@Override
		public int getCount() {
			return (opItems == null) ? 0 : opItems.length;
		}

		@Override
		public Object getItem(int arg0) {
			return opItems[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int index, View convertView, ViewGroup viewG) {// 每次都会将布局重新加载一次
																			// 快速滚动有性能瓶颈
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(// 加载我们传入的布局文件
						R.layout.contacts_list_items, null);
				viewHolder = new ViewHolder();
				viewHolder.contactsName = (TextView) view.findViewById(R.id.contact_name);
				viewHolder.contactsCheck = (CheckBox) view.findViewById(R.id.contact_check);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.contactsName.setTextColor(Color.BLACK);
			viewHolder.contactsCheck.setTextColor(Color.BLACK);
			viewHolder.contactsName.setText(opItems[index]);
			viewHolder.contactsCheck.setChecked(isSelected[index]);
		
			return view;
		}

		class ViewHolder {// 对控件的实例进行缓存
			TextView contactsName;
			CheckBox contactsCheck;
		}
	}

	/** 进度条显示 */
	private void showCustomProcessingBar() {
		dlg = new AlertDialog.Builder(mContext).create();
		dlg.setCancelable(false);
		dlg.show();

		Window window = dlg.getWindow();

		LinearLayout mLinearLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams mLinearLayoutlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);// 在JAVA中动态构建的布局
		// mLinearLayoutlp.setMargins(100, 0, 100, 0);
		mLinearLayout.setLayoutParams(mLinearLayoutlp);
		// mLinearLayout.setPadding(100, 0, 100, 0);
		mLinearLayout.setGravity(Gravity.CENTER);
		mLinearLayout.setBackgroundColor(Color.WHITE);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		TextView title = new TextView(mContext);
		title.setGravity(Gravity.CENTER);
		title.setBackgroundColor(R.color.title);
		title.setTextSize(25);
		title.setText("填充进度");

		LinearLayout mLinearLayout1 = new LinearLayout(mContext);
		mLinearLayout1.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout1.setGravity(Gravity.CENTER);

		updateMsg = new TextView(mContext);
		updateMsg.setPadding(0, 50, 0, 50);
		updateMsg.setGravity(Gravity.CENTER);
		updateMsg.setTextSize(22);
		updateMsg.setText("正在填充");

		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mLinearLayout1.addView(updateMsg, lp);

		mLinearLayout.addView(title, lp);
		mLinearLayout.addView(mLinearLayout1, lp);
		window.setContentView(mLinearLayout);

	}

	/**
	 * 返回手机号码
	 */
	private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,182,183,184,187,188,133,153,177,180,181,189, 130,131,132,155,156,185,186,176"
			.split(",");

	private static String getTel() {
		int index = getNum(0, telFirst.length - 1);
		String first = telFirst[index];
		String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
		String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
		return first + second + third;
	}

	public static int getNum(int start, int end) {
		return (int) (Math.random() * (end - start + 1) + start);
	}


	/* android程序执行adb shell命令 */
	private static void exec(String command) {
		Process process = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(command);
			process.waitFor();
		} catch (Exception e) {
		}
	}

}
