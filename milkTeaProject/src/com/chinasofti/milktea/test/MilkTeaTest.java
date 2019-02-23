package com.chinasofti.milktea.test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//import com.mysql.jdbc.Connection
import org.junit.Test;

import com.chinasofti.milktea.dao.framework.JdbcDao;
import com.chinasofti.milktea.dao.milktea.milkmeamaoimpl.MilkTeaDaoImpl;

/**
 * 
 * 
*
* @author wzh
* @version 创建时间：2019年2月18日 下午2:23:07
* 类说明
 */
public class MilkTeaTest {
	private Connection conn=JdbcDao.getConnection();
	private MilkTeaDaoImpl milkTeaDaoImpl = new MilkTeaDaoImpl();
	public static void main(String[] args) {
		MilkTeaTest milkTeaTest = new MilkTeaTest();
		while(true) {
			milkTeaTest.login();
		}
	}
	@Test
	public void login() {
		Scanner sc = new Scanner(System.in);
		String username = null;
		String password = null;
		boolean flag = true;
		Map<String,String> hashMap = new HashMap<String,String>(5000);
		String sql = "SELECT * FROM login;";
		while (flag) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String user = rs.getString("username");
				String pass = rs.getString("password");
				hashMap.put(user, pass);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
			System.out.println("请输入登录用户名：");
			username = sc.nextLine();
			if (hashMap.containsKey(username)) {
				System.out.println("请输入密码：");
				password = sc.nextLine();
				if (hashMap.get(username).equals(password)) {
					flag = false;
					milkTeaDaoImpl.login(username, password);
					break;
				} else {
					System.out.println("密码错误！请重新输入！");
				}
			}else {
				System.out.println("用户名不存在！请注册！");
				System.out.println("请输入要注册的用户名：");
				String registerUserName=sc.nextLine();
				System.out.println("请输入初始密码：");
				String registerUserPassWord=sc.nextLine();
				milkTeaDaoImpl.register(registerUserName,registerUserPassWord);
			}
		}
	}
}
