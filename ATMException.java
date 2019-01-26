package ATM;
import java.lang.Exception;
public class ATMException extends Exception{
String msg;
	public ATMException(String msg)
	{
		this.msg=msg;
	}
	public String toString(){
		{
			return this.msg;//返回此异常消息
		}
	}
}
