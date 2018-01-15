package mail.test.page;
//page 层将用 户动作 操作封装成一个个方法 方便public去调用
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoginPage {
	private static WebElement element= null;
	//WebDriver driver=new FirefoxDriver();
	public static WebElement login_userName(WebDriver driver){
		driver.switchTo().frame("x-URS-iframe");
		element = driver.findElement(By.name("emal"));
		return element;
	}
	public static WebElement login_passWord(WebDriver driver){
		driver.switchTo().frame("x-URS-iframe");
		element = driver.findElement(By.name("password"));
		return element;
	}
	public static WebElement login_button(WebDriver driver){
		driver.switchTo().frame("x-URS-iframe");
		element = driver.findElement(By.id("dologin"));
		return element;
	}
	public static WebElement login_user(WebDriver driver){
		driver.switchTo().frame("x-URS-iframe");
		element = driver.findElement(By.id("spnUid"));
		return element;
	}
	public static WebElement logout_button(WebDriver driver){
		driver.switchTo().frame("x-URS-iframe");
		element =driver.findElement(By.linkText("退出"));
				return element;
	}
	//添加错误提示信息的元素定位
	public static WebElement login_Error_Hint(WebDriver driver){
		element = driver.findElement(By.xpath("//div[@class='error-tt']/p"));
		return element;
	}
	/*driver.findElement(By.name("email")).clear();
	driver.findElement(By.name("email")).sendKeys("testingwtb");
	driver.findElement(By.name("password")).clear();
	driver.findElement(By.name("password")).sendKeys("a123456");
	driver.findElement(By.id("dologin")).click();
	
	
	//从frame中切回主界面，切到frame后，我们不能继续操作主界面，这个时候如果想操作主界面，则需要切回主界面
			driver.switchTo().defaultContent();*/
}
