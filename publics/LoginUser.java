package mail.test.publics;

import org.openqa.selenium.WebDriver;

import mail.test.page.LoginPage;

public class LoginUser {
	//登陆方法
	public static void login(WebDriver driver,String username,String password){
		LoginPage.login_userName(driver).clear();
		LoginPage.login_userName(driver).sendKeys(username);
		LoginPage.login_passWord(driver).clear();
		LoginPage.login_passWord(driver).sendKeys(password);
		LoginPage.login_button(driver).click();
	}
	
	//登陆用户获取信息
	public static String login_user_info(WebDriver driver){
		String  text= LoginPage.login_user(driver).getText();
		return text;
	}
	//获取登陆错误提示
	public static String login_error_info(WebDriver driver){
		String text = LoginPage.login_Error_Hint(driver).getText();
		return text;
	}
	//退出方法
	public static void logout(WebDriver driver){
		LoginPage.logout_button(driver);
	}
}
