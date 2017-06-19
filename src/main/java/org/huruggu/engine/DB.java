package org.huruggu.engine;



import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;

import java.sql.*;
import java.util.ArrayList;

public class DB {
	private static JDBCClient client = null;
	PreparedStatement preparedStmt = null;
	
	public static void connection(Vertx vertx) {
		client = JDBCClient.createShared(vertx, Config.db());
	}

	/*
	
	public ResultSet query(String qeury) {
		try {
			Statement stmt = (Statement) DB.conn.createStatement();
			ResultSet rs = stmt.executeQuery(qeury);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet prepareQuery(String query, ArrayList list) throws SQLException {
		//String query = " insert into users (first_name, last_name, date_created, is_admin, num_points)" + " values (?, ?, ?, ?, ?)";
		try {
		 	// create the mysql insert preparedstatement
			preparedStmt = DB.conn.prepareStatement(query);
			
			for(int i = 0; i < list.size(); i++) {
	            //Helper.printDebug("two index " + i + " : value " + list.get(i));
	    		if(list.get(i) instanceof String) {
					preparedStmt.setString (i+1, (String) list.get(i));
	    		} else if(list.get(i) instanceof Integer) {
	    			preparedStmt.setInt (i+1, (Integer) list.get(i));
	    		}
	        }
			// execute the preparedstatement
			return preparedStmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void closePreparedStmt() {
		if (preparedStmt != null) {
			try {
				preparedStmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int prepareQueryU(String query, ArrayList list) throws SQLException {
		//String query = " insert into users (first_name, last_name, date_created, is_admin, num_points)" + " values (?, ?, ?, ?, ?)";
		try {
		 	// create the mysql insert preparedstatement
			preparedStmt = DB.conn.prepareStatement(query);
			
			for(int i = 0; i < list.size(); i++) {
	            //Helper.printDebug("two index " + i + " : value " + list.get(i));
	    		if(list.get(i) instanceof String) {
					preparedStmt.setString (i+1, (String) list.get(i));
	    		} else if(list.get(i) instanceof Integer) {
	    			preparedStmt.setInt (i+1, (Integer) list.get(i));
	    		}
	        }
			// execute the preparedstatement
			return preparedStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closePreparedStmt();
		}
		
		return 0;
	}
	
	public static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
*/
}
