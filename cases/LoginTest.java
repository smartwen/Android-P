package mail.test.cases;

import java.util.concurrent.TimeUnit;

import mail.test.publics.LoginUser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;

public class LoginTest {
	private WebDriver driver;
	private String baseUrl;
	
	@BeforeMethod
	public void setUp() throws Exception{
		driver=new FirefoxDriver();
		baseUrl="http://www.126.com/";
		driver.manage().timeouts().implicitlyWait(3000, TimeUnit.SECONDS);
	}
	@Test
	public void testCase() throws Exception{
		driver.get(baseUrl);
		String username="15012800343";
		String password="admin123LW";
		LoginUser.login(driver, username, password);
		Thread.sleep(5000);
		//ÑéÖ¤
		String text=LoginUser.login_user_info(driver);
		Assert.assertEquals(text, username+"@126.com");
	}
	
	@AfterMethod
	public void tearDown() throws Exception{
		LoginUser.logout(driver);
		driver.quit();
	}
// 	test
}
