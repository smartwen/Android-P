package root.weather52;

import java.awt.Point;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import com.oppo.autotest.phenixaw.app.AW_Weather;
import com.oppo.autotest.phenixaw.app.Common;
import com.oppo.phenix.framework.constants.PublicEnums.CompareType;
import com.oppo.phenix.framework.constants.ServerConstants.AttrType;
import com.oppo.phenix.framework.server.AccessibilityNodeInfo;

import oppo.autotest.phenix.PhenixPhones;
import oppo.autotest.phenix.Phone;
import oppo.autotest.phenix.log.PhenixLog;

/**
 * 功能域：天气
 * 
 * 用例ID：136782
 * 
 * 用例编号：Weather_030
 * 
 * 用例标题: 更新天气
 * 
 * 前置条件: 联通卡数据连接正常 测试点: 手动更新
 * 
 * 用例内容: 进入天气，下拉主界面刷新，查看天气是否能正常更新
 * 
 * 检查点: 1、会提示更新成功、天气显示正常，能正常更新最近5天的天气，且更新时间准确 2、其他指数能正常显示
 * 
 * 文本用例作者：卢达明
 * 
 * 脚本代码作者：刘文
 * 
 * 手机版本：PAFM00_11.A.15_0152_201807120331
 * 
 * 应用版本：
 * 
 * 创建时间：2018-07-19 09:53:02
 */
public class Weather_030 {

	private static Throwable sThrowable = new Throwable();

	public static boolean test(String[] args) {
		PhenixPhones phones = null;
		Phone phone0 = null;
		Common common = null;
		AW_Weather weather = null;
		try {
			phones = PhenixPhones.getInstance(args);
			// 设置脚本资源及脚本phonelog保存路径
			phones.resourcePathSet("%ResourcePath%/res/Weather_030/");
			// 获取设备引用对象
			phone0 = phones.getPhone();
			// 设置录制时手机的分辨率，实现回放时不同分辨率的自适应
			phone0.recordResolutionSet(1080, 2340);

			common = new Common(phone0);
			weather = new AW_Weather(phone0);
			common.aw_home();

			// 打开定位服务
			phone0.gpsStateSet(true);

			// 打开数据网络
			if (!phone0.sim.openNetwork()) {
				throw new Exception("数据网络开启失败，请检查是否插入SIM卡");
			}
			phone0.waitForStability();

			// 若wifi已开启，则关闭
			if (phone0.wifiIsOpen()) {
				phone0.wifiEnableSet(false);
			}

			// 清除应用缓存数据：天气
			weather.clearWeatherDataAndService();
			// 启动应用：天气
			if (!phone0.appOpen("com.coloros.weather", "com.coloros.weather.OppoMainActivity")) {
				throw new Exception("启动应用:天气, 失败");
			}
			phone0.waitForStability();

			// 处理位置信息弹窗
			common.ClickPoPup();
			weather.clickWeatherPopup();

			// 等待页面加载
			weather.waitForPageLoading();

			// 获取屏幕宽度、高度
			Point point = phone0.resolutionXYGet();
			int w = point.x;
			int h = point.y;
			phone0.sleep(2000);
			boolean flag = false;
			// 滑动屏幕，下拉刷新
			if (!phone0.screenSwipeNoAdapt((w / 2), (h * 1 / 4), (w / 2), (h * 7 / 8), 80)) {
				throw new Exception("滑动屏幕失败");
			}

			// 插入检查点，检查界面更新更新成功
			for (int i = 0; i < 80; i++) {
				if (phone0.textContainCheck("更新成功")) {
					System.out.println("------------");
					flag = true;
					break;
				}
				phone0.sleep(50);
				if (i == 50) {
					if (!phone0.screenSwipeNoAdapt((w / 2), (h * 1 / 4), (w / 2), (h * 7 / 8), 80)) {
						throw new Exception("滑动屏幕失败");
					}
				}
			}
			phone0.waitForStability();

			if (!flag) {
				throw new Exception("检查界面是否更新成功，执行失败");
			}
			phone0.waitForStability();
			phone0.sleep(2000);

			// 获取当前时间
			String systemtime = phone0.systemTimeGet();
			int curhour = Integer.valueOf(systemtime.substring(systemtime.indexOf(" ") + 1, systemtime.indexOf(":")));

			// 获取更新日期、更新时间
			String updatedate = phone0.nodeInfoGetByClass("android.view.View", 1).getContentDescription().toString();
			String updatetime = phone0.nodeInfoGetByClass("android.view.View", 3).getContentDescription().toString();
			updatetime = updatetime.substring(0, updatetime.indexOf(" "));

			// 检查点1：能够天气自动定位
			if (phone0.descContainCheck("[--, 左右滑动切换城市]")) {
				if (phone0.descContainCheck("[当前天气信息为空]")) {
					throw new Exception("检查点1：能够天气自动定位，执行失败");
				}
			}
			phone0.waitForStability();

			// 检查点2：自动更新日期和时间是否正确
			if (!(updatedate.contains("今天") && updatetime.equals(curhour + "时"))) {
				throw new Exception("检查点2：自动更新日期和时间是否正确，执行失败");
			}
			phone0.waitForStability();

			// 获取天气状态，查看页面上方是否展示空气质量
			String airQ = phone0.nodeInfoGetByClass("android.widget.TextView", 2).getText().toString();
			boolean airFlag = false;
			if (airQ.contains("空气")) {
				airFlag = true;
			}

			for (int i = 1; i < 6; i++) {
				// 检查点 能正常更新最近5天的天气
				Calendar c1 = Calendar.getInstance();
				c1.add(Calendar.DAY_OF_YEAR, i);
				System.out.println(c1.getTime());
				Format f1 = new SimpleDateFormat("yyyy_MM_dd hh:mm:ss");
				System.out.println(f1.format(c1.getTime()));
				String c = f1.format(c1.getTime());
				int m = Integer.parseInt(c.substring(c.indexOf("_") + 1, c.lastIndexOf("_")));
				int d = Integer.parseInt(c.substring(c.lastIndexOf("_") + 1, c.indexOf(" ")));
				phone0.sleep(2000);

				String date = m + "月" + d + "日";
				List<AccessibilityNodeInfo> list = phone0.getAllNodeInfo(AttrType.DESC, CompareType.CONTAIN, date);
				System.out.println(list.size());
				String str = null;
				for (int j = 0; j < list.size(); j++) {
					str = list.get(j).getContentDescription().toString();
					System.out.println(str + "-------");
					if (str.contains(date)) {
						str = str.substring(str.indexOf("周") + 3, str.lastIndexOf(" "));
						System.out.println(str);
					}
				}

				// 检查点3：能正常更新最近5天的天气，且更新时间准确---检查 日期 m/d 的天气
				if (phone0.descContainCheck(date)) {
					if (!(str.contains("多云") || str.contains("中雨") || str.contains("晴") || str.contains("小雨")
							|| str.contains("阴"))) {
						throw new Exception("检查：" + date + "的天气是否存在" + str + "，执行失败");
					}
				}
				phone0.waitForStability();
			}

			// 2、向下滑动屏幕
			phone0.scrollDownToBottom();

			// 查找紫外线
			if (!phone0.textContainCheck("紫外线")) {
				if (!phone0.listSwipeAndSearchByText("未来15日天气预报")) {
					throw new Exception("查找未来15日天气预报，执行失败");
				}
				phone0.waitForStability();
			}
			phone0.waitForStability();

			if (!phone0.textContainCheck("紫外线")) {
				throw new Exception("查找紫外线，执行失败");
			}
			phone0.waitForStability();

			// 检查点4 其他指数能正常显示
			boolean matchCN2 = Pattern.matches("体感温度 " + "[0-9][0-9]" + "℃",
					phone0.nodeInfoGetByTextContain("体感温度").getText().toString());
			boolean matchCN3 = Pattern.matches("湿度\\s[0-9][0-9]%$",
					phone0.nodeInfoGetByTextContain("湿度").getText().toString());
			boolean matchCN4 = Pattern.matches("能见度\\s[0-9]{1,3}\\s千米$",
					phone0.nodeInfoGetByTextContain("能见度").getText().toString());
			boolean matchCN7 = Pattern.matches("气压\\s[0-9]{3,4}\\s百帕$",
					phone0.nodeInfoGetByTextContain("气压").getText().toString());

			if (!(matchCN2 && matchCN3 && matchCN4 && matchCN7 && phone0.textContainCheck("紫外线"))) {
				throw new Exception("检查点4：获取其他指数能正常显示----体感温度等，执行失败");
			}
			phone0.waitForStability();
			String str_wind = phone0.nodeInfoGetByTextContain("风").getText().toString();// 获取风向风力
			String str_uv = phone0.nodeInfoGetByTextContain("紫外线").getText().toString();// 获取紫外线
			if (str_wind == null && str_uv == null) {
				throw new Exception("检查点4：获取其他指数能正常显示----风向等，执行失败");
			}
			phone0.waitForStability();

			phone0.scrollDownToBottom();

			if (airFlag) {
				for (int i = 0; i < 6; i++) {
					if (!phone0.textContainCheck(new String[] { "PM10", "PM2.5", "O3", "NO2", "SO", "CO" }[i])) {
						throw new Exception("检查点4：获取其他指数能正常显示---空气质量区域，执行失败");// PM2.5信息
					}
					phone0.waitForStability();
				}
			}

			common.aw_home();
		} catch (Exception e) {
			sThrowable = e;
			phones.logSave();
			return false;
		} finally {
			try {
				// 清除应用缓存数据：天气
				weather.clearWeatherDataAndService();
				// 初始化天气定位、网络
				weather.netGpsInitialize();
				if (!phone0.pingTest("www.baidu.com", 100)) {
					throw new Exception("打开百度，连接网络，失败");
				}
				phone0.waitForStability();
				// 回桌面
				common.aw_home();
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
