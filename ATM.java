package ATM;

import java.util.Scanner;

public class ATM {
	public static Scanner in=new Scanner(System.in);
	public static int choice;
	private static Account account;
	
	//显示主菜单
	public static void showMenu()
	{
		System.out.println("*****欢迎来到深圳招行ATM系统******");
		System.out.println("\t1.开户");
		System.out.println("\t2.查询余额");
		System.out.println("\t3.存款");
		System.out.println("\t4.取款");
		System.out.println("\t5.修改密码");
		System.out.println("\t6.退出");
		System.out.println("请选择：");
		choice=in.nextInt();
	}
	//开户方法
	public static void initAccount()
	{
		account=new Account();
		System.out.println("开户成功 以下是你开户的信息");
		System.out.println(account);//调用toString
	}
	//查询余额方法
	public static void showBalance()
	{
		System.out.println(account.getBalance());
	}
	//存款的方法
	public static boolean addBalance(int number)
	{
		try {
			if(number<0)
			{
				throw new ATMException("金额不能为负数");
				
			}else
			{
				account.setBalance(account.getBalance()+number);
				System.out.println("存款成功");
				return true;
			}
		} catch (ATMException e) {
			e.printStackTrace();
			return false;
		}
	}
	//取款的方法
	public static boolean reduceBalance(int number)
	{
		try {
			if(number<=0)
			{
				throw new ATMException("取款金额不能为负数");
				
			}else if(number>account.getBalance())
			{
				throw new ATMException("账户余额不足");
			}else 
			{
				account.setBalance(account.getBalance()-number);
				System.out.println("取款成功");
				return true;
			}
		} catch (ATMException e) {
			e.printStackTrace();
			return false;
		}
	}
	//密码验证方法
	public static boolean passVerifty(String pass)
	{
		try {
			if(pass.equals("")||pass==null)
			{
				throw new ATMException("密码不能为空");
			}else if(!pass.equals(account.getPassword()))
			{
				throw new ATMException("密码验证失败");
			}else
			{
				System.out.println("密码验证成功");
				return true;
			}
		} catch (ATMException ex) {
			//e.printStackTrace();
			System.out.println(ex);
			return false;
		}
			
	}
	//修改密码方法
	public static boolean changePass(String pass)
	{
		try {
			if(pass.equals("")||pass.trim()==null)
			{
				throw new ATMException("密码不能为空");
			}else if(pass.length()!=6)
			{
				throw new ATMException("密码长度不能少于6位");
			}else
			{
				account.setPassword(pass);
				System.out.println("密码修改成功");
				return true;
			}
		} catch (ATMException e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
	}
	public static void main(String[] args) {
		ATM.showMenu();
		do {
			if(choice==6)
			{
				break;
			}
			switch(choice)
			{
			case 1:
				ATM.initAccount();
				break;
			case 2:
				ATM.showBalance();
				break;
			case 3:
				System.out.println("请输入存款金额：");
				int number=in.nextInt();
				ATM.addBalance(number);
				break;
			case 4:
				System.out.println("请输入取款金额：");
				int reduceNumber=in.nextInt();
				ATM.reduceBalance(reduceNumber);
				break;
			case 5:
				System.out.println("请输入原有密码");
				String pass=in.next();
				if(ATM.passVerifty(pass))
				{
					System.out.println("请输入新密码：");
					 pass=in.next();
					ATM.changePass(pass);
				}
				break;
			}			
		ATM.showMenu();//用户选择完后再次选择
		}while(choice!=6);
		System.out.println("感谢你的使用 朝朝盈ATM系统");
	}
}
