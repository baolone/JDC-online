package com.jdc.app.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

	private static Properties prop;
	private static String url;
	private static String usr;
	private static String pwd;
	
	static {
		try {
			prop = new Properties();
			prop.load(new FileInputStream("application.properties"));
			url = prop.getProperty("db.url");
			usr = prop.getProperty("db.usr");
			pwd = prop.getProperty("db.pwd"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, usr, pwd);
	}
}
