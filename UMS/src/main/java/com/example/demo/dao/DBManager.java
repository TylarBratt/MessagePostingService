package com.example.demo.dao;

import java.sql.*;  

public class DBManager {
	
 public Connection con;
 
	public DBManager() {
	try{  
		Class.forName("com.mysql.jdbc.Driver");  
		con=DriverManager.getConnection(  
		"jdbc:mysql://localhost:3306/umsdb","root","root");  
		}catch(Exception e){ 
			
			System.out.println(e);}  
		}  

	
	
	
	public void generalOperation(Connection con) {
		try {
		Statement stmt=con.createStatement();  
		ResultSet rs=stmt.executeQuery("select * from TWEET");  
		while(rs.next()) {
		System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
		}}catch(Exception e) {
			System.out.println(e);
		}
	}
}
