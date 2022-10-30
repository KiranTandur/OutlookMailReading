package com.test.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import com.test.business.UtilityClass;

public class GetDbConnection {
	
	public static Connection getConnection() {
		Connection connection = null;
		try {
			// below two lines are used for database connectivity.
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_database", "root", "oracle");

			if (connection != null) {
				return connection;
			}else {
				System.out.println("DataBase Connectivity couldn't established.....!!!");
				return null;
			}
		} catch (Exception exception) {
			System.out.println(exception);
			return null;
		} 
	}

}
