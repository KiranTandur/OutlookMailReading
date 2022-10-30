package com.test.readmail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.test.business.UtilityClass;

public class TestMainClassDummy {

	public static void main(String[] args) throws SQLException {
		Connection connection = null;
		try {
			// below two lines are used for database connectivity.
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_database", "root", "oracle");

			if (connection != null) {
				System.out.println("Please Enter the Subject line to display the emails associated with it...");
				Scanner scan = new Scanner(System.in);
				String subject = scan.nextLine();
				if(subject!=null) {
					UtilityClass utilityClass = new UtilityClass();
					utilityClass.getDetailsBySubject(connection, subject);
				}
			}else {
				System.out.println("DataBase Connectivity couldn't established.....!!!");
			}
		} catch (Exception exception) {
			System.out.println(exception);
		} 
	}
}
