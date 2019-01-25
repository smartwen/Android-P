package com.oppotest.commtools.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.Settings;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Mms.Addr;
import android.provider.Telephony.Mms.Part;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Threads;
import android.provider.oppo.Telephony;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.SmsApplication;
import com.oppotest.commtools.R;

/** 填充相关方法类 */
public class FillMethods {
	private Context mContext;

	public FillMethods(Context context) {
		mContext = context;
	}

	/*********************************************** 通话记录相关 ***************************************************/

	/** 设置未接来电数量 */
	public boolean setMissedCallCount(int missed_call_count) {
		return Settings.System.putInt(mContext.getContentResolver(),
				"oppo.missed.calls.number", missed_call_count);
	}

	/** 填充通话记录
	 * tag:？
	 * date 日期
	 * duration 通话时间
	 * number 号码
	 * type 类型:已接 未接 已拨
	 * ring_time 响铃时间 
	 * geocoded_location 归属地
	 *  */
	public void fillCallLogs(int tag, long date, long duration, String number,
			int type, int ring_time, String geocoded_location) {
		//ContentValues 和HashTable类似都是一种存储的机制,以(key,value)的形式来存储数据。
		ContentValues values = new ContentValues();
		values.put(FillUtils.CallLog.NEW, tag);
		values.put(FillUtils.CallLog.DATE, date);
		values.put(FillUtils.CallLog.DURATION, duration);
		values.put(FillUtils.CallLog.NUMBER, number);
		values.put(FillUtils.CallLog.TYPE, type);
		values.put(FillUtils.CallLog.RING_TIME, ring_time);
		values.put(FillUtils.CallLog.GEOCODED_LOCATION, geocoded_location);
		// Log.e("tx", geocoded_location);
		//访问内容提供器中数据 getContentResolver得到实例ContentResolver CRUD 操作
		mContext.getContentResolver().insert(FillUtils.CallLog.CONTENT_URI,
				values);
	}

	/** 查询通话记录数量 */
	public int queryCallLogsNum() {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(FillUtils.CallLog.CONTENT_URI, null,
				null, null, null);
		// // 查询通话记录的详细内容
		// if (cursor != null) {
		// while (cursor.moveToNext()) {
		// int k = cursor.getInt(cursor
		// .getColumnIndex(FillUtils.CallLog.RING_TIME));
		// String j = cursor.getString(cursor
		// .getColumnIndex(FillUtils.CallLog.NUMBER));
		// String i = cursor.getString(cursor
		// .getColumnIndex(FillUtils.CallLog.GEOCODED_LOCATION));
		// Log.d("tx", i + ":" + j + "," + k);
		// }
		// }
		int num = cursor.getCount();
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return num;
	}

	/** 清除所有通话记录 */
	public boolean clearCallLogs() {
		int has = queryCallLogsNum();
		if (has <= 0)
			return true;

		ContentResolver resolver = mContext.getContentResolver();
		int deleted = resolver
				.delete(FillUtils.CallLog.CONTENT_URI, null, null);
		return has == deleted ? true : false;
	}

	/*********************************************** 通话记录相关 ***************************************************/

	/*********************************************** 联系人相关 *****************************************************/


	/** 获取本机所有联系人号码 */
	public ArrayList<String> getAllContactNumbers() {
		ArrayList<String> phoneList = new ArrayList<String>();
		Cursor cursor = mContext.getContentResolver().query(Phone.CONTENT_URI, FillUtils.Contacts.PHONES_COLS, null,
				null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String phoneNumber = cursor.getString(FillUtils.Contacts.PHONE_NUMBER_INDEX);
				int nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
				String contact = cursor.getString(nameIndex);
				String strPhoneInfo = contact + "\n" + phoneNumber;
				phoneList.add(strPhoneInfo);
				//phoneList = null;
			}
			cursor.close();
		}
		return phoneList;
	}
	

	/** 查询当前电话本联系人数量 */
	public int queryPhoneContactsNum() {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursorPhone = resolver.query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		int totalNum = 0;
		if (null != cursorPhone) {
			totalNum = cursorPhone.getCount();
		}
		if (null != cursorPhone && !cursorPhone.isClosed()) {
			cursorPhone.close();
			cursorPhone = null;
		}
		return totalNum > 0 ? totalNum : 0;
	}

	/** 删除联系人 */
	public boolean clearAllContacts() {
		return deleteAllContacts();
	}

	/** 填充联系人 */
	public void fillContacts(boolean nameF, boolean phonesF, boolean emailsF,
			boolean websF, boolean photoF, boolean addressF, boolean birthdayF,
			boolean qqF, boolean noteF, boolean orgF, boolean relationF,
			boolean nickF, int index) {

		// 生成电话本填充值
		CreateContactsData contactsData = new CreateContactsData(mContext);
		String name = nameF ? contactsData.CreateNamedata(index) : null;
		String[] phones = phonesF ? contactsData.CreatePhonedata(11, 3)
				: contactsData.CreatePhonedata(11, 1);
		String[] emails = emailsF ? contactsData.CreateEmaildata(10, 1) : null;
		String[] webs = websF ? contactsData.CreateWebsdata(1) : null;
		byte[] photo = photoF ? contactsData.CreatePhotodata() : null;
		String[] address = addressF ? contactsData.CreateAddressdata(10) : null;
		String[] birthday = birthdayF ? contactsData.CreateBirthdaydata(6)
				: null;
		String[] qq = qqF ? contactsData.CreateQQdata(10) : null;
		String[] note = noteF ? contactsData.CreateNotedata(10) : null;
		String org = orgF ? contactsData.CreateOrgdata(10) : null;
		String relation = relationF ? contactsData.CreateNamedata(4) : null;
		String nick = nickF ? contactsData.CreateNamedata(2) : null;
		// 填充到电话本数据库
		saveContactsToDB(name, phones, emails, webs, photo, address, birthday,
				qq, note, org, relation, nick);
	}

	/** 保存到联系人数据库 */
	private boolean saveContactsToDB(String name, String[] phones,
			String[] emails, String[] webs, byte[] photo, String[] address,
			String[] birthday, String[] qq, String[] note, String org,
			String relation, String nick) {
/*通过ContentProviderOperation和ContentResolver#applyBatch()可以批量处理数据, 
		例如一次插入多条数据, 或者同时向一个ContentProvider中的不同表插入数据
		这个组合的主要作用是保持一系列操作的原子性, 即要么所有操作都成功, 要么所有操作都不成功.
*/
		final ArrayList<ContentProviderOperation> operationList = buildOperationForRecord(
				name, phones, emails, webs, photo, address, birthday, qq, note,
				org, relation, nick);

		boolean retVal = false;
		ContentResolver resolver = mContext.getContentResolver();
		try {
			retVal = true;
			resolver.applyBatch(ContactsContract.AUTHORITY, operationList);
		} catch (RemoteException e) {
			retVal = false;
		} catch (OperationApplicationException e) {
			retVal = false;
		}
		return retVal;
	}

	/** 构建填充数据 */
	private static ArrayList<ContentProviderOperation> buildOperationForRecord(
			String name, String[] phones, String[] emails, String[] webs,
			byte[] photo, String[] address, String[] birthday, String[] qq,
			String[] note, String org, String relation, String nick) {
		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		//使批量更新、插入、删除数据更加方便，android系统引入了 ContentProviderOperation类
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(RawContacts.CONTENT_URI);
		builder.withValue("aggregation_needed", 0);//定义一列的数据值。只在更新、插入数据中有用
		builder.withValue("aggregation_mode",
				RawContacts.AGGREGATION_MODE_DISABLED);
		operationList.add(builder.build());

		ArrayList<ContentProviderOperation> nameOpList = buildNamesInsert(name);
		ArrayList<ContentProviderOperation> phoneOpList = buildPhoneInsert(phones);
		ArrayList<ContentProviderOperation> emailOpList = buildEmailsInsert(emails);
		ArrayList<ContentProviderOperation> webOpList = buildWebsInsert(webs);
		ArrayList<ContentProviderOperation> photoOpList = buildPhotoInsert(photo);

		ArrayList<ContentProviderOperation> addressOpList = buildAddressInsert(address);
		ArrayList<ContentProviderOperation> birthdayOpList = buildBirthdayInsert(birthday);
		ArrayList<ContentProviderOperation> qqOpList = buildQQInsert(qq);
		ArrayList<ContentProviderOperation> noteOpList = buildNoteInsert(note);
		ArrayList<ContentProviderOperation> orgOpList = buildOrgInsert(org);
		ArrayList<ContentProviderOperation> relationOpList = buildRelationInsert(relation);
		ArrayList<ContentProviderOperation> nickOpList = buildNickInsert(nick);
		// ArrayList<ContentProviderOperation> groupOpList =
		// buildGroupInsert(group);

		if (null != nameOpList && 0 < nameOpList.size())
			operationList.addAll(nameOpList);
		if (null != phoneOpList && 0 < phoneOpList.size())
			operationList.addAll(phoneOpList);
		if (null != emailOpList && 0 < emailOpList.size())
			operationList.addAll(emailOpList);
		if (null != webOpList && 0 < webOpList.size())
			operationList.addAll(webOpList);
		if (null != photoOpList && 0 < photoOpList.size())
			operationList.addAll(photoOpList);

		if (null != addressOpList && 0 < addressOpList.size())
			operationList.addAll(addressOpList);
		if (null != birthdayOpList && 0 < birthdayOpList.size())
			operationList.addAll(birthdayOpList);
		if (null != qqOpList && 0 < qqOpList.size())
			operationList.addAll(qqOpList);
		if (null != noteOpList && 0 < noteOpList.size())
			operationList.addAll(noteOpList);
		if (null != orgOpList && 0 < orgOpList.size())
			operationList.addAll(orgOpList);
		if (null != relationOpList && 0 < relationOpList.size())
			operationList.addAll(relationOpList);
		if (null != nickOpList && 0 < nickOpList.size())
			operationList.addAll(nickOpList);

		return operationList;
	}

	// 姓名
	private static ArrayList<ContentProviderOperation> buildNamesInsert(
			String name) {
		if (null == name)
			return null;

		String names = name;
		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;
		//调用下面静态函数来获取一个Builder 对象
		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);//创建一个用于执行插入操作的Builder
		builder.withValueBackReference(StructuredName.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		builder.withValue(StructuredName.DISPLAY_NAME, names);

		// builder.withValue(StructuredName.FAMILY_NAME, names.substring(0, 1));
		// builder.withValue(StructuredName.GIVEN_NAME, names.substring(length -
		// 1, length));
		// builder.withValue(StructuredName.MIDDLE_NAME, names.substring(1,
		// names.length() - 1));
		// builder.withValue(StructuredName.PREFIX, names.substring(0, 1));
		// builder.withValue(StructuredName.SUFFIX, names.substring(length - 1,
		// length));
		// 拼音
		// builder.withValue(StructuredName.PHONETIC_FAMILY_NAME,
		// getPinYin(abc.substring(0, 1)));
		// builder.withValue(StructuredName.PHONETIC_MIDDLE_NAME,
		// getPinYin(abc.substring(1, 2)));
		// builder.withValue(StructuredName.PHONETIC_GIVEN_NAME,
		// getPinYin(abc.substring(2)));
		operationList.add(builder.build());
		return operationList;
	}

	// 电话
	private static ArrayList<ContentProviderOperation> buildPhoneInsert(
			String[] phones) {
		if (null == phones)
			return null;
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;
		int[] phone_address = { Phone.TYPE_MOBILE, Phone.TYPE_HOME,
				Phone.TYPE_WORK, Phone.TYPE_MAIN, Phone.TYPE_COMPANY_MAIN,
				Phone.TYPE_FAX_HOME, Phone.TYPE_FAX_WORK };

		for (int i = 0; i < phones.length; i++) {
			if (i >= phone_address.length)
				break;

			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(Phone.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			builder.withValue(Phone.TYPE, phone_address[i]);
			builder.withValue(Phone.NUMBER, phones[i]);
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
		}
		return operationList;

	}

	// 网址
	private static ArrayList<ContentProviderOperation> buildWebsInsert(
			String[] webs) {
		if (webs == null) {
			return null;
		}

		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		for (int i = 0; i != webs.length; i++) {
			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(Website.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE);
			builder.withValue(Website.TYPE, Website.TYPE_HOME);
			builder.withValue(Website.URL, webs[i]);
			// operationList.add(builder.build());
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
		}
		return operationList;
	}

	// 电子邮箱
	private static ArrayList<ContentProviderOperation> buildEmailsInsert(
			String[] emails) {
		if (emails == null) {
			return null;
		}
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;
		int[] email_address = { Email.TYPE_HOME, Email.TYPE_WORK,
				Email.TYPE_MOBILE, Email.TYPE_OTHER };
		for (int a = 0; a < emails.length; a++) {
			// for (int i = 0; i != emails.length; i++) {
			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(Email.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			builder.withValue(Email.TYPE, email_address[a]);
			builder.withValue(Email.DATA, emails[a]);
			builder.withValue(Email.DISPLAY_NAME, emails[a]);

			// operationList.add(builder.build());
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
			// }

		}
		return operationList;
	}

	// 头像
	private static ArrayList<ContentProviderOperation> buildPhotoInsert(
			byte[] photo) {
		if (photo == null) {
			return null;
		}
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(Photo.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
		builder.withValue(Photo.PHOTO, photo);
		// operationList.add(builder.build());
		if (!hasSetPrimary) {
			builder.withValue(Data.IS_PRIMARY, 1);
			hasSetPrimary = true;
		}
		operationList.add(builder.build());

		return operationList;
	}

	// 地址
	private static ArrayList<ContentProviderOperation> buildAddressInsert(
			String[] address) {
		if (null == address)
			return null;
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		for (int i = 0; i != address.length; i++) {
			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(StructuredPostal.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME);
			builder.withValue(StructuredPostal.FORMATTED_ADDRESS, address[i]);
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
		}
		return operationList;

	}

	// 生日
	private static ArrayList<ContentProviderOperation> buildBirthdayInsert(
			String[] birthday) {
		if (null == birthday)
			return null;
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		for (int i = 0; i != birthday.length; i++) {
			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(Event.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE);
			builder.withValue(Event.TYPE, Event.TYPE_BIRTHDAY);
			builder.withValue(Event.DATA, birthday[i]);
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
		}
		return operationList;

	}

	// qq
	private static ArrayList<ContentProviderOperation> buildQQInsert(String[] qq) {
		if (null == qq)
			return null;
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		for (int i = 0; i != qq.length; i++) {
			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(Im.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
			builder.withValue(Im.TYPE, Im.PROTOCOL_QQ);
			builder.withValue(Im.DATA, qq[i]);
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
		}
		return operationList;

	}

	// 备注
	private static ArrayList<ContentProviderOperation> buildNoteInsert(
			String[] note) {
		if (null == note)
			return null;
		boolean hasSetPrimary = false;

		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		for (int i = 0; i != note.length; i++) {
			builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			builder.withValueBackReference(Note.RAW_CONTACT_ID, 0);
			builder.withValue(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
			// builder.withValue(Note.NOTE,Note.);
			builder.withValue(Note.NOTE, note[i]);
			if (!hasSetPrimary) {
				builder.withValue(Data.IS_PRIMARY, 1);
				hasSetPrimary = true;
			}
			operationList.add(builder.build());
		}
		return operationList;

	}

	// 组织
	private static ArrayList<ContentProviderOperation> buildOrgInsert(
			String names) {
		if (null == names)
			return null;
		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(Organization.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);

		builder.withValue(Organization.COMPANY, names);
		builder.withValue(Organization.TITLE, names + "5");

		operationList.add(builder.build());

		return operationList;
	}

	// 关系
	private static ArrayList<ContentProviderOperation> buildRelationInsert(
			String relation) {
		if (null == relation)
			return null;
		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(Relation.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, Relation.CONTENT_ITEM_TYPE);

		builder.withValue(Relation.TYPE, Relation.TYPE_ASSISTANT);
		builder.withValue(Relation.NAME, relation);

		operationList.add(builder.build());

		return operationList;
	}

	// 昵称
	private static ArrayList<ContentProviderOperation> buildNickInsert(
			String nick) {
		if (null == nick)
			return null;
		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = null;

		builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		builder.withValueBackReference(Nickname.RAW_CONTACT_ID, 0);
		builder.withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE);

		builder.withValue(Nickname.NAME, nick);

		operationList.add(builder.build());

		return operationList;
	}

	private class CreateContactsData {
		private int insertNum;
		private String names;
		private Bitmap image;
		private String[] phones;
		private String[] birthdays;
		private String[] addresses;
		private String[] emails;
		private String[] webs;
		private String[] qqs;
		private String[] notes;
		private String[] groups;
		private byte[] photoStream;

		private Context mContext;
		private Resources res;

		public CreateContactsData(Context context) {
			mContext = context;
			res = mContext.getResources();
		}

		public String CreateNamedata(int index) {
			// FIXME
			// insertNum = length;
			// String[] nameArrays =
			// mContext.getResources().getStringArray(R.array.fillcontacts_name_data);
			// Random rd = new Random();
			// int ran = rd.nextInt(nameArrays.length);
			//
			// names = String.valueOf(insertNum) + nameArrays[ran];
			int total = FillUtils.Contacts.FirstName.length;
			Random rd = new Random();
			int first = rd.nextInt(total);
			int last = rd.nextInt(total);

			names = FillUtils.Contacts.FirstName[first]
					+ FillUtils.Contacts.FirstName[last] + index;
			return names;
		}

		public String[] CreatePhonedata(int length, int num) {
			insertNum = length;
			String[] phoneArrays = mContext.getResources().getStringArray(
					R.array.fillcontacts_phone_data);
			phones = new String[num];
			for (int i = 0; i < num; i++) {
				Random rd = new Random();
				int ran = rd.nextInt(phoneArrays.length);
				phones[i] = phoneArrays[ran];
			}
			return phones;
		}

		public String[] CreateAddressdata(int length) {
			insertNum = length;
			String special_word = res.getString(R.string.special_word);
			char[] special_words = special_word.toCharArray();
			int random;
			random = (int) (Math.random() * special_words.length + 1);
			addresses = new String[] { String.valueOf(insertNum)
					+ special_words[random / 2] + special_words[random - 1]
					+ special_words[special_words.length - random]
					+ special_words[random / 3 * 2]
					+ special_words[random / 5 * 2]
					+ special_words[random / 7 * 2]
					+ special_words[random / 9 * 2]
					+ special_words[random / 5 * 3]
					+ special_words[random / 5 * 4] };
			return addresses;
		}

		public String[] CreateEmaildata(int length, int num) {
			insertNum = length;
			String english_word = res.getString(R.string.english_word);
			char[] english_words = english_word.toCharArray();
			emails = new String[num];
			for (int i = 0; i < num; i++) {
				int random;
				random = (int) (Math.random() * 50 + 1);
				emails[i] = String.valueOf(insertNum)
						+ english_words[english_words.length - random]
						+ "@oppo.com";
			}
			return emails;
		}

		public String[] CreateWebsdata(int length) {
			insertNum = length;
			webs = new String[] { "www.oppo.com" };
			return webs;
		}

		public String[] CreateBirthdaydata(int length) {
			insertNum = length;
			birthdays = new String[] { String.valueOf(1988 + insertNum % 20)
					+ "-" + String.valueOf(insertNum % 12 + 1) + "-"
					+ String.valueOf(insertNum % 28 + 1) };
			return birthdays;
		}

		public String[] CreateQQdata(int length) {
			insertNum = length;
			long qqbase = 80000000l;
			int random = (int) (Math.random() * 10000);
			long qq = qqbase + insertNum + insertNum * 100 + random;
			qqs = new String[] { String.valueOf(qq) };
			return qqs;
		}

		public String[] CreateNotedata(int length) {
			insertNum = length;
			String special_word = res.getString(R.string.special_word);
			char[] special_words = special_word.toCharArray();
			int random;
			random = (int) (Math.random() * special_words.length + 1);
			notes = new String[] { String.valueOf(insertNum)
					+ special_words[random / 3] + special_words[random / 2]
					+ special_words[special_words.length - random]

					+ special_words[random / 3 * 2]
					+ special_words[random / 7 * 2]

					+ special_words[random / 9 * 5]
					+ special_words[random / 7 * 4] };
			return notes;
		}

		@SuppressWarnings("unused")
		public String[] CreateGroupdata(int length) {
			insertNum = 0;
			groups = new String[] { "family" };
			return groups;
		}

		public byte[] CreatePhotodata() {
			image = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.icon_home);
			photoStream = bitmapToBytes(image);
			return photoStream;
		}

		public String CreateOrgdata(int length) {
			insertNum = length;
			String[] nameArrays = mContext.getResources().getStringArray(
					R.array.fillcontacts_name_data);
			Random rd = new Random();
			int ran = rd.nextInt(nameArrays.length);
			names = String.valueOf(insertNum) + nameArrays[ran];
			return names;
		}

		private byte[] bitmapToBytes(Bitmap bitmap) {
			if (null == bitmap)
				return null;
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			return os.toByteArray();
		}
	}

	public boolean deleteAllContacts() {
		if (queryPhoneContactsNum() <= 0)
			return true;

		long[] ids = getRawContactsIdsOfAccount();
		if (ids == null || ids.length == 0) {
			return false;
		}
		boolean success = false;
		int size = ids.length;

		int[] newlen = new int[1];
		for (int from = 0; from < size; from = from
				+ FillUtils.Contacts.DELETE_EACHTIME) {
			String idsel = buildIds(ids, from, from
					+ FillUtils.Contacts.DELETE_EACHTIME, newlen);
			success = deletePhoneContactsByRawIds(idsel);
			if (!success)
				break;
		}
		return true;
	}

	private String buildIds(long[] ids, int from, int to, int[] out) {
		if (to > ids.length) {
			to = ids.length;
		}

		long[] inputIds = new long[to - from];
		for (int i = 0; i != inputIds.length; i++) {
			inputIds[i] = ids[i + from];
		}

		out[0] = to - from;
		return buildIdsStringForSQLQuery(inputIds);
	}

	private static String buildIdsStringForSQLQuery(final long[] ids) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");

		for (int i = 0; i != ids.length; i++) {
			builder.append(ids[i]);
			builder.append(",");
		}

		builder.setLength(builder.length() - 1); // Delete the last comma.
		builder.append(")");

		return builder.toString();
	}

	private long[] getRawContactsIdsOfAccount() {
		long[] ids = null;
		String where = RawContacts.DELETED + "=0";

		Cursor cursor = mContext.getContentResolver().query(
				RawContacts.CONTENT_URI, new String[] { RawContacts._ID },
				where, null, null);
		if (cursor != null) {
			int count = cursor.getCount();
			if (count > 0) {
				int index = 0;
				ids = new long[count];
				while (cursor.moveToNext()) {
					ids[index] = cursor.getLong(0);
					index++;
				}
			}
			cursor.close();
		}
		return ids;
	}

	private boolean deletePhoneContactsByRawIds(String rawids) {
		ContentResolver resolver = mContext.getContentResolver();
		String where = RawContacts._ID + " IN " + rawids;
		resolver.delete(RawContacts.CONTENT_URI, where, null);
		return true;
	}

	/*********************************************** 联系人相关 *****************************************************/

	/************************************************ 信息相关 ******************************************************/
	/** 获取当前短信数量 */
	public int querySmsNum() {
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(Uri.parse("content://sms"), null,
				null, null, null);
		int has = cursor.getCount();
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return has;
	}

	@SuppressLint("NewApi")
	public boolean clearAllSms() {
		int has = querySmsNum();
		if (has <= 0) {
			return true;
		}
		ContentResolver contentResolver = mContext.getContentResolver();
		int deleted = contentResolver.delete(Sms.CONTENT_URI, null, null);
		return has == deleted ? true : false;
	}

	@SuppressLint("NewApi")
	public int queryMmsNum() {
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(Mms.CONTENT_URI, null, null,
				null, null);
		int has = cursor.getCount();
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return has;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public boolean clearAllMms() {
		int has = queryMmsNum();
		if (has <= 0) {
			return true;
		}
		ContentResolver contentResolver = mContext.getContentResolver();
		int deleted = contentResolver.delete(Mms.CONTENT_URI, null, null);
		return has == deleted ? true : false;
	}

	@SuppressLint("NewApi")
	public synchronized void fillSmsData(String phoneNum, String smsBody,
			int readFlag, int locked, int smstype) {
		String[] dests = formatPhoneName(phoneNum);
		HashSet<String> recipients = new HashSet<String>();
		int len = dests.length;
		for (int i = 0; i < len; i++)
			recipients.add(dests[i]);
		long threadId = Threads.getOrCreateThreadId(mContext, recipients);

		writeSmsDataBase(threadId, System.currentTimeMillis(), smsBody,
				getAndJudgeInputNum(phoneNum), readFlag, locked, smstype);
	}

	/** 格式化电话号码 */
	private String getAndJudgeInputNum(String phoneNum) {
		String defaultNum = "18600000000";
		if (phoneNum == null || phoneNum.trim().equals("")) {
			return defaultNum;
		}
		long singlePhonel = Long.valueOf(phoneNum);
		if (-1l < singlePhonel && singlePhonel < 19999999999l) {
			return String.valueOf(singlePhonel);
		} else {
			return defaultNum;
		}
	}

	/** 格式化地址 */
	private static String[] formatPhoneName(String address) {
		List<String> numbers = new ArrayList<String>();
		for (String number : address.split(";")) {
			if (!TextUtils.isEmpty(number))
				numbers.add(number);
		}
		return numbers.toArray(new String[numbers.size()]);
	}

	/** 插入数据库 */
	@SuppressLint("InlinedApi")
	private void writeSmsDataBase(long threadId, long date, String smsBody,
			String phonenum, int readFlag, int locked, int smsType) {
		ContentResolver contentResolver = mContext.getContentResolver();
		String[] SMS = mContext.getResources().getStringArray(
				R.array.Sms_Send_Message);
		int arr = new Random().nextInt(51);
		ContentValues values = new ContentValues(9);
		values.put(Sms.ADDRESS, phonenum);
		values.put(Sms.DATE, date + FillUtils.SMS.DATE_INTEVAL);
		values.put(Sms.READ, readFlag);
		if (smsBody != null) {
			values.put(Sms.BODY, smsBody);
		} else {
			values.put(Sms.BODY, SMS[arr]);
		}
		values.put(Sms.STATUS, -1);
		values.put(Sms.TYPE, smsType);
		values.put(Sms.SEEN, 1);
		values.put(Sms.LOCKED, locked);
		values.put(Sms.THREAD_ID, threadId);
		contentResolver.insert(Uri.parse("content://sms"), values);
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	public void fillMms(int msgBoxType, int type, int isRead, int locked,
			String address) {
		long threadId = Threads.getOrCreateThreadId(mContext, address);

		String name_1 = null;
		String name_2 = null;
		String smil_text = null;
		ContentValues cv_part_1 = null;
		ContentValues cv_part_2 = null;

		String longS = mContext.getString(R.string.MmsContact);

		switch (type) {
		case 0:
			name_1 = FillUtils.SMS.IMAGE_NAME_1;
			name_2 = FillUtils.SMS.IMAGE_NAME_2;
			smil_text = String.format(FillUtils.SMS.SMIL_TEXT_IMAGE, name_1,
					name_2);
			cv_part_1 = createPartRecord(0, "image/jpeg", name_1,
					FillUtils.SMS.IMAGE_CID, name_1, null, null);
			cv_part_2 = createPartRecord(0, "image/jpeg", name_2,
					FillUtils.SMS.IMAGE_CID.replace("cid", "cid_2"), name_2,
					null, null);
			break;
		case 1:
			name_1 = FillUtils.SMS.AUDIO_NAME;
			name_2 = FillUtils.SMS.AUDIO_NAME;
			smil_text = String.format(FillUtils.SMS.SMIL_TEXT_AUDIO, name_1,
					name_2);
			cv_part_1 = createPartRecord(0, "audio/MID", name_1,
					FillUtils.SMS.AUDIO_CID, name_1, null, null);
			cv_part_2 = createPartRecord(0, "audio/MID", name_2,
					FillUtils.SMS.AUDIO_CID.replace("cid", "cid_2"), name_2,
					null, null);
			break;
		case 2:
			name_1 = FillUtils.SMS.VIDEO_NAME;
			name_2 = FillUtils.SMS.VIDEO_NAME;
			smil_text = String.format(FillUtils.SMS.SMIL_TEXT_VIDEO, name_1,
					name_2);
			cv_part_1 = createPartRecord(0, "video/3gpp", name_1,
					FillUtils.SMS.VIDEO_CID, name_1, null, null);
			cv_part_2 = createPartRecord(0, "video/3gpp", name_2,
					FillUtils.SMS.VIDEO_CID.replace("cid", "cid_2"), name_2,
					null, null);
			break;
		case 3:
			longS = mContext.getString(R.string.MmsContactLong);
			name_1 = FillUtils.SMS.AUDIO_NAME;
			name_2 = FillUtils.SMS.AUDIO_NAME;
			smil_text = String.format(FillUtils.SMS.SMIL_TEXT_TEXT, name_1,
					name_2);
			cv_part_1 = createPartRecord(0, "text/txt", name_1,
					FillUtils.SMS.AUDIO_CID, name_1, null, null);
			cv_part_2 = createPartRecord(0, "text/txt", name_2,
					FillUtils.SMS.AUDIO_CID.replace("cid", "cid_2"), name_2,
					null, null);
			break;
		}

		// make MMS record
		ContentValues cvMain = new ContentValues();
		cvMain.put(Mms.THREAD_ID, threadId);

		cvMain.put(Mms.MESSAGE_BOX, msgBoxType);
		cvMain.put(Mms.READ, isRead);
		cvMain.put(Mms.LOCKED, locked);
		cvMain.put(Mms.DATE, System.currentTimeMillis() / 1000);
		cvMain.put(Mms.SUBJECT, mContext.getString(R.string.MmsContactSubject));

		cvMain.put(Mms.CONTENT_TYPE, "application/vnd.wap.multipart.related");
		cvMain.put(Mms.MESSAGE_CLASS, "personal");
		cvMain.put(Mms.MESSAGE_TYPE, 132); // Retrive-Conf Mms
		cvMain.put(Mms.MESSAGE_SIZE, getSize(name_1) + getSize(name_2) + 512);
		cvMain.put(Mms.PRIORITY, String.valueOf(129));
		cvMain.put(Mms.READ_REPORT, String.valueOf(129));
		cvMain.put(Mms.DELIVERY_REPORT, String.valueOf(129));

		Random random = new Random();
		cvMain.put(Mms.MESSAGE_ID, String.valueOf(random.nextInt(100000)));
		cvMain.put(Mms.TRANSACTION_ID, String.valueOf(random.nextInt(120000)));

		long msgId = 0;
		try {
			msgId = ContentUris.parseId(mContext.getContentResolver().insert(
					Mms.CONTENT_URI, cvMain));
		} catch (Exception e) {
			Log.e("", "insert pdu record failed", e);
			return;
		}

		// make parts
		ContentValues cvSmil = createPartRecord(-1, "application/smil",
				"smil.xml", "<siml>", "smil.xml", null, smil_text);
		cvSmil.put(Part.MSG_ID, msgId);

		cv_part_1.put(Part.MSG_ID, msgId);
		cv_part_2.put(Part.MSG_ID, msgId);

		ContentValues cv_text_1 = createPartRecord(0, "text/plain",
				"text_1.txt", "<text_1>", "text_1.txt", null, null);
		cv_text_1.put(Part.MSG_ID, msgId);
		cv_text_1.remove(Part.TEXT);
		cv_text_1.put(Part.TEXT, "slide 1 text" + longS);
		cv_text_1.put(Part.CHARSET, "106");

		ContentValues cv_text_2 = createPartRecord(0, "text/plain",
				"text_2.txt", "<text_2>", "text_2.txt", null, null);
		cv_text_2.put(Part.MSG_ID, msgId);
		cv_text_2.remove(Part.TEXT);
		cv_text_2.put(Part.TEXT, "slide 2 text" + longS);
		cv_text_2.put(Part.CHARSET, "106");

		// insert parts
		Uri partUri = Uri.parse("content://mms/" + msgId + "/part");
		try {
			mContext.getContentResolver().insert(partUri, cvSmil);

			Uri dataUri_1 = mContext.getContentResolver().insert(partUri,
					cv_part_1);
			if (!copyData(dataUri_1, name_1)) {
				Log.e("", "write " + name_1 + " failed");
				return;
			}
			mContext.getContentResolver().insert(partUri, cv_text_1);

			Uri dataUri_2 = mContext.getContentResolver().insert(partUri,
					cv_part_2);
			if (!copyData(dataUri_2, name_2)) {
				Log.e("", "write " + name_2 + " failed");
				return;
			}
			mContext.getContentResolver().insert(partUri, cv_text_2);
		} catch (Exception e) {
			Log.e("", "insert part failed", e);
			return;
		}

		// to address
		ContentValues cvAddr = new ContentValues();
		cvAddr.put(Addr.MSG_ID, msgId);
		cvAddr.put(Addr.ADDRESS, "703");
		cvAddr.put(Addr.TYPE, "151");
		cvAddr.put(Addr.CHARSET, 106);
		mContext.getContentResolver().insert(
				Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);

		// from address
		cvAddr.clear();
		cvAddr.put(Addr.MSG_ID, msgId);
		cvAddr.put(Addr.ADDRESS, FillUtils.SMS.FROM_NUM);
		cvAddr.put(Addr.TYPE, "137");
		cvAddr.put(Addr.CHARSET, 106);
		mContext.getContentResolver().insert(
				Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);
	}

	private int getSize(final String name) {
		InputStream is = null;
		int size = 0;

		try {
			is = mContext.getAssets().open(name);
			byte[] buffer = new byte[1024];
			for (int len = 0; (len = is.read(buffer)) != -1;)
				size += len;
			return size;
		} catch (FileNotFoundException e) {
			Log.e("", "failed to found file?", e);
			return 0;
		} catch (IOException e) {
			Log.e("", "write failed..", e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				Log.e("", "close failed...");
			}
		}
		return 0;
	}

	@SuppressLint("InlinedApi")
	private ContentValues createPartRecord(int seq, String ct, String name,
			String cid, String cl, String data, String text) {
		// cv_part_1 = createPartRecord(0, "image/jpeg", name_1, IMAGE_CID,
		// name_1, null, null);
		ContentValues cv = new ContentValues(8);
		cv.put(Part.SEQ, seq);
		cv.put(Part.CONTENT_TYPE, ct);
		cv.put(Part.NAME, name);
		cv.put(Part.CONTENT_ID, cid);
		cv.put(Part.CONTENT_LOCATION, cl);
		if (data != null)
			cv.put(Part._DATA, data);
		if (text != null)
			cv.put(Part.TEXT, text);
		return cv;
	}

	private boolean copyData(Uri dataUri, String name) {
		InputStream input = null;
		OutputStream output = null;

		try {
			input = mContext.getAssets().open(name);
			output = mContext.getContentResolver().openOutputStream(dataUri);

			byte[] buffer = new byte[1024];
			for (int len = 0; (len = input.read(buffer)) != -1;)
				output.write(buffer, 0, len);
		} catch (FileNotFoundException e) {
			FillUtils.logE("failed to found file?" + e);
			return false;
		} catch (IOException e) {
			FillUtils.logE("write failed.." + e);
			return false;
		} finally {
			try {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			} catch (IOException e) {
				FillUtils.logE("close failed...");
				return false;
			}
		}
		return true;
	}

	/** 获取默认短信应用 */
	public String getDefaultSmsApp(Context context) {
		return Telephony.Sms.getDefaultSmsPackage(context);
	}

	/** 设置默认短信应用 */
	public void setDefaultSmsApp(Context context, String packageName) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			if (!packageName.equals(getDefaultSmsApp(context)))
				SmsApplication.setDefaultApplication(packageName, context);
		}
	}

	/************************************************ 短信相关 ******************************************************/

}
