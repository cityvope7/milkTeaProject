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
* @version ����ʱ�䣺2019��2��18�� ����2:23:07
* ��˵��
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
		
		
			System.out.println("�������¼�û�����");
			username = sc.nextLine();
			if (hashMap.containsKey(username)) {
				System.out.println("���������룺");
				password = sc.nextLine();
				if (hashMap.get(username).equals(password)) {
					flag = false;
					milkTeaDaoImpl.login(username, password);
					break;
				} else {
					System.out.println("����������������룡");
				}
			}else {
				System.out.println("�û��������ڣ���ע�ᣡ");
				System.out.println("������Ҫע����û�����");
				String registerUserName=sc.nextLine();
				System.out.println("�������ʼ���룺");
				String registerUserPassWord=sc.nextLine();
				milkTeaDaoImpl.register(registerUserName,registerUserPassWord);
			}
		}
	}
}
