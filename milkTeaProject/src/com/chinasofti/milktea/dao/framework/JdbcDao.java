package com.chinasofti.milktea.dao.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * 
*
* @author wzh
* @version 创建时间：2019年2月18日 下午2:23:07
* 类说明
 */
public class JdbcDao {
	private static String DriverClassName="com.mysql.jdbc.Driver";
	private static String url="jdbc:mysql://localhost:3306/mydb";
	private static String user="root";
	private static String password="wzh123456...";
	private static Connection conn=null;
	
	public static Connection getConnection() {
		try {
			Class.forName(DriverClassName);
			conn=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void closeConnection() {
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
