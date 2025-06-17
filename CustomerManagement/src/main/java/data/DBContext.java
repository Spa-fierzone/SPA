/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBContext {
	public static Connection con = null;
	public static final String url = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=studen;";
	public static final String username = "sa";
	public static final String password = "123";

	static {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println("Error connect to database");
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	public static void main(String[] args) {
		try {
			System.out.println(new DBContext().getConnection());
			System.out.println("Success");
		} catch (Exception e) {
			System.out.println("Failed");
		}
	}
}