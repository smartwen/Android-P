package root.contacts52;

import com.oppo.autotest.phenixaw.app.AW_Contact;
import com.oppo.autotest.phenixaw.app.AW_SystemTextClick;
import com.oppo.autotest.phenixaw.app.Common;
import com.oppo.phenix.framework.constants.PublicEnums.CompareType;
import com.oppo.phenix.framework.constants.ServerConstants.AttrType;
import com.oppo.phenix.framework.constants.ServerConstants.SimNo;
import com.oppo.phenix.framework.constants.ServerConstants.SwitchName;
import com.oppo.phenix.framework.constants.ServerConstants.SwitchState;
import com.oppo.phenix.framework.constants.ServerConstants.TelecomOperator;

import oppo.autotest.phenix.PhenixPhones;
import oppo.autotest.phenix.Phone;
import oppo.autotest.phenix.log.PhenixLog;

/**
 * 功能域：电话本
 * 
 * 用例ID：132086
 * 
 * 用例编号：Contacts_319
 * 
 * 用例标题: 导入/导出联系人
 * 
 * 前置条件: 电话本有联系人 测试点: 发送电话本
 * 
 * 用例内容: 设置-电话本-导入/导出联系人-发送电话本 1、选择信息
 * 
 * 检查点: 1、跳转到新建信息界面，能够发送VCF格式的联系文件，联系人VCF文件可以彩信形式发送
 * 
 * 文本用例作者：卢达明
 * 
 * 脚本代码作者：彭碧
 * 
 * 手机版本：CPH1871EX_11.A.06_0060_201807091217
 * 
 * 应用版本：
 * 
 * 创建时间：2018-07-12 16:44:44
 */
public class Contacts_319 {

	private static Throwable sThrowable = new Throwable();

	public static boolean test(String[] args) {
		PhenixPhones phones = null;
		Phone phone0 = null;
		Phone phone1 = null;
		Common common = null;
		Common common1 = null;
		AW_SystemTextClick system = null;
		AW_SystemTextClick system1 = null;
		try {
			phones = PhenixPhones.getInstance(args);
			// 设置脚本资源及脚本phonelog保存路径
			phones.resourcePathSet("%ResourcePath%/res/Contacts_319/");
			// 获取设备引用对象
			phone0 = phones.getPhone();
			phone1 = phones.getPhone();
			// 设置录制时手机的分辨率，实现回放时不同分辨率的自适应
			phone0.recordResolutionSet(1080, 2280);
			phone1.recordResolutionSet(1080, 2280);

			common = new Common(phone0);
			common1 = new Common(phone1);
			AW_Contact contact = new AW_Contact(phone1);
			system = new AW_SystemTextClick(phone0);
			system1 = new AW_SystemTextClick(phone1);

			// 辅助机和测试机分别进行返回桌面
			common.aw_home();
			common1.aw_home();

			// 打开工具平台输入法
			phone0.setSwitchState(SwitchName.ATF_INPUT_METHOD, SwitchState.OPEN);
			phone1.setSwitchState(SwitchName.ATF_INPUT_METHOD, SwitchState.OPEN);

			// 删除备份与恢复
			phone0.fileDelete("/sdcard/Backup");

			// 获取测试机中卡的数量
			int simCount = phone0.sim.getSimCount();
			String testnum = null;
			String testType1 = null;

			if (simCount < 1) {
				throw new Exception("测试机中卡的数量小于1，执行失败");
			} else {
				// 获取测试机号码
				testnum = phone0.sim.getPhoneNumber(SimNo.SIM1);
				System.out.println("测试机号码" + testnum);
				if (testnum == null || testnum.equals("")) {
					testnum = phone0.sim.getPhoneNumber(SimNo.SIM2);
					System.out.println("测试机号码" + testnum);
				}
				// 获取测试机类型
				testType1 = phone0.providerNameGet(0);
				System.out.println("测试机卡1的类型--" + testType1);
				if (testType1.equals("No_sim") || testType1 == null || testType1.equals("")) {
					testType1 = phone0.providerNameGet(1);
					System.out.println("测试机卡2的类型--" + testType1);
				}
			}
			phone0.waitForStability();
			
			// 获取辅助机号码
			String auxiliaryNum = phone1.sim.getPhoneNumber(SimNo.SIM1);
			System.out.println("辅助机号码" + auxiliaryNum);
			if (auxiliaryNum == null || auxiliaryNum.equals("")) {
				auxiliaryNum = phone1.sim.getPhoneNumber(SimNo.SIM2);
				System.out.println("辅助机号码" + auxiliaryNum);
			}

			// 清空联系人
			phone0.contactsClear();
			phone1.contactsClear();

			// 填充5个联系人
			for (int i = 0; i < 5; i++) {
				phone0.phoneContactsFill("填充联系人测试" + i, "1577326220" + i);
				phone0.waitForStability();
			}

			phone0.appKill("all");
			phone1.appKill("all");
			phone0.sleep(2000);

			// 初始化启动测试机信息
			system.OpenProgram(true, "com.android.mms", "com.android.mms.ui.ConversationList");
			phone0.waitForStability();
			// 初始化启动辅助机信息
			system1.OpenProgram(true, "com.android.mms", "com.android.mms.ui.ConversationList");
			phone1.waitForStability();
			common.ClickPoPup();
			common1.ClickPoPup();

			common.aw_home();
			phone0.sleep(2000);

			// 启动设置
			system.OpenProgram(true, "com.android.settings", "com.oppo.settings.SettingsActivity");
			phone0.waitForStability();

			phone0.sleep(4000);

			system.swipeSearchAndClick("系统应用", "电话本", null, null, null, null);
			phone0.waitForStability();

			// 处理声明与条款的弹框
			common.ClickPoPup();

			// 点击导入/导出联系人
			if (!phone0.screenClickByText(0, "导入/导出联系人")) {
				throw new Exception("点击导入/导出联系人，执行失败");
			}
			phone0.waitForStability();

			// 点击发送电话本
			if (!phone0.screenClickByText(0, "发送电话本", 1)) {
				throw new Exception("点击发送电话本，执行失败");
			}
			phone0.waitForStability();

			phone0.sleep(4000);

			String sendName = "信息";

			// 测试机检查是否有sendName，点击sendName
			for (int j = 0; j < 5; j++) {
				if (phone0.descContainCheck(sendName)) {
					if (!(phone0.screenClickByDesc(0, sendName) || phone0.screenClickByText(0, sendName))) {
						throw new Exception("测试机点击" + sendName + ", 失败");
					}
					phone0.waitForStability();
					break;
				}
				if (!phone0.screenSwipeInWidget(AttrType.CLASS, CompareType.EQUAL, "android.view.View", 0, 0.98, 0.5,
						0.05, 0.5, 40)) {
					throw new Exception("当前页面没有" + sendName + "，执行左滑，左滑后，检查是否有" + sendName + ", 失败");
				}
				phone0.waitForStability();
			}
			phone0.waitForStability();

			// 处理声明与条款
			common.ClickPoPup();
			phone0.waitForStability();

			// 页面跳转到新信息页面
			phone0.sleep(4000);

			// 检查点1：跳转到新建信息界面
			if (!(phone0.textContainCheck("新信息") && phone0.textContainCheck("彩信") && phone0.textContainCheck("收件人："))) {
				throw new Exception("点击发送电话本，执行失败");
			}
			phone0.waitForStability();

			// 清空phone1的短信，确保后面的数据不被污染
			phone1.mmsClear();
			phone1.waitForStability();

			// 输入收件人电话
			if (!phone0.textInputDirectly(auxiliaryNum)) {
				throw new Exception("输入收件人电话, 失败");
			}
			phone0.waitForStability();

			phone0.sleep(3000);
			String str = testType1 + "1发送";

			System.out.println(phone1.smsNumQuery() + "------------1");

			// 点击发送
			if (!(phone0.screenClickByText(0, "SIM1发送") || phone0.screenClickByText(0, str)
					|| phone0.screenClickByDesc(0, "发送")
					|| phone0.screenClickById(0, "com.android.mms:id/send_button"))) {
				throw new Exception("点击发送短信按钮, 失败");
			}
			phone0.sleep(2000);

			// 检查点1：可以成功经信息发送，出现拨号和详情
			if (!(phone0.widgetExistCheckByDesc("拨号") && phone0.widgetExistCheckByDesc("详情"))) {
				throw new Exception("检查点1：可以成功经信息发送，出现拨号和详情, 失败");
			}
			phone0.waitForStability();

			common.aw_back();

			for (int q = 0; q < 100; q++) {
				phone1.sleep(3000);
				if (phone1.mmsNumQuery() == 1) {
					break;
				}
			}
			
			// 检查点2：可以成功经信息发送,辅助机的信息条数为1
			if (phone1.mmsNumQuery() != 1) {
				throw new Exception("检查点2：可以成功经信息发送,辅助机的信息条数为1，失败");
			}
			phone0.waitForStability();

			common1.aw_home();
			phone1.appKill("all");
			// 启动辅助机信息
			system1.OpenProgram(false, "com.android.mms", "com.android.mms.ui.ConversationList");
			phone1.waitForStability();
			common1.ClickPoPup();
			// 点击陌生信息
			if (phone1.textContainCheck("陌生信息")) {
				if (!phone1.screenClickByText(0, "陌生信息")) {
					throw new Exception("点击：陌生信息,执行失败");
				}
			}
			phone1.waitForStability();

			if (phone1.textContainCheck("无陌生信息")) {
				common1.aw_back();
			}
			phone1.waitForStability();

			// 辅助机点击测试机号码进入到信息界面
			if (!(phone1.screenClickByText(0, testnum) || phone1.screenClickByText(0, testnum.substring(3))
					|| phone1.screenClickByText(0, "+86" + testnum))) {
				throw new Exception("辅助机点击测试机号码进入到信息界面,执行失败");
			}
			phone1.waitForStability();

			phone1.sleep(3000);

			// 检查点3：能够发送VCF格式的联系文件，联系人VCF文件可以彩信形式发送
			if (!phone1.descContainCheck("彩信附件")) {
				if (phone1.textContainCheck("彩信大小")) {
					if (!phone0.screenClickById(0, "com.android.mms:id/btn_download_msg")) {
						throw new Exception("点击下载彩信,执行失败");
					}
					phone1.waitForStability();
					phone1.sleep(10000);
				} else {
					throw new Exception("检查点3：能够发送VCF格式的联系文件，联系人VCF文件可以彩信形式发送,执行失败");
				}
				phone1.waitForStability();
			}
			phone1.waitForStability();

			phone1.sleep(3000);

			// 点击彩信附件
			if (!phone1.screenClickByDesc(0, "彩信附件")) {
				throw new Exception("点击彩信附件,执行失败");
			}
			phone1.waitForStability();

			phone1.sleep(3000);

			// 点击导入
			if (phone1.textContainCheck("这是联系人备份文件")) {
				if (!phone1.screenClickByText(0, "导入")) {
					throw new Exception("点击导入,执行失败");
				}
				phone1.waitForStability();
			}
			phone1.waitForStability();

			phone1.sleep(10000);

			common1.aw_home();

			// 打开电话本
			contact.aw_startContact();

			// 点击联系人
			if (!phone1.screenClickByClass(0, "android.widget.Button", 1)) {
				throw new Exception("点击联系人, 失败");
			}
			phone1.waitForStability();

			// 检查点4：成功导入导入电话本文件
			for (int j = 0; j < 5; j++) {
				if (!phone1.textContainCheck("填充联系人测试" + j)) {
					throw new Exception("检查点4：成功导入导入电话本文件，缺少填充联系人测试" + j + ", 失败");
				}
				phone1.waitForStability();
			}
			phone1.waitForStability();

			// 辅助机和测试机分别进行返回桌面
			common.aw_home();
			common1.aw_home();

		} catch (Exception e) {
			sThrowable = e;
			phones.logSave();
			return false;
		} finally {
			try {
				common.aw_home();
				// 清空联系人
				phone0.contactsClear();

				// 清空phone0的短信，确保后面的数据不被污染
				phone0.mmsClear();
				phone0.waitForStability();

				// 清空phone1的短信，确保后面的数据不被污染
				phone1.mmsClear();
				phone1.waitForStability();

				// 初始化信息
				if (!phone0.appInitialize("com.android.mms")) {
					throw new Exception("初始化信息，执行失败");
				}
				phone0.waitForStability();
				if (!phone1.appInitialize("com.android.mms")) {
					throw new Exception("初始化信息，执行失败");
				}
				phone1.waitForStability();

				// 清除文件
				if (!phone0.fileDelete("/sdcard/Backup")) {
					throw new Exception("删除文件夹失败");
				}
				phone0.waitForStability();

				phone0.appKill("all");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			phones.close(null);
		}
		return true;
	}

	public static void main(String[] args) {
		if (test(args)) {
			PhenixLog.log("PhenixTest:Pass");
		} else {
			StringBuilder errorInfo = new StringBuilder();
			if (sThrowable != null) {
				errorInfo = errorInfo.append(sThrowable).append("\n");
				StackTraceElement[] trace = sThrowable.getStackTrace();
				for (StackTraceElement traceElement : trace) {
					errorInfo.append("\tat ").append(traceElement).append("\n");
				}
			}
			PhenixLog.logE("PhenixTest:Fail\n" + errorInfo);
		}
		PhenixLog.log("ExitFlag");
		System.exit(0);
	}

}
