package mail.test.cases;

import java.util.concurrent.TimeUnit;

import mail.test.publics.LoginUser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;

public class LoginText2 {
	private WebDriver driver;
	private String baseUrl;
	
	@BeforeMethod
	public void setUp() throws Exception{
		driver=new FirefoxDriver();
		baseUrl="http://www.126.com/";
		driver.manage().timeouts().implicitlyWait(3000, TimeUnit.SECONDS);
	}
	
	//定义对象数组
	@DataProvider(name="user")
	public Object[][] Users(){
		return new Object[][]{
				{"","","请先输入您的邮箱帐号"},
				{"testing","","帐号或密码错误"},
				{"","123456","请先输入您的邮箱帐号"},
				{"error","error","帐号或密码错误"},
		};
	}
	@Test(dataProvider="user")
	public void testCase(String username,String password,String expectText) throws InterruptedException{
		driver.get(baseUrl);
		Thread.sleep(5000);
		LoginUser.login(driver, username, password);
		Thread.sleep(2000);
		String text=LoginUser.login_error_info(driver);
		System.out.println(text);
		System.out.println(expectText);
		Assert.assertEquals(text, expectText);
	}

	
	@AfterMethod
	public void tearDown() throws Exception{
		LoginUser.logout(driver);
		driver.quit();
	}
}
