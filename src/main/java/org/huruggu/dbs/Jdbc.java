package org.huruggu.dbs;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import java.util.List;


public class Jdbc {
    private static SQLClient sqlClient = null;

    public static void initialize(Vertx vertx, JsonObject config) {
        Jdbc.sqlClient = JDBCClient.createShared(vertx, config.getJsonObject("jdbc"), "Jdbc");
    }

    public static SQLClient getSQLClient() {
        return Jdbc.sqlClient;
    }

    public static void updateWithParams(String query, JsonArray datas, SQLConnection connection, Handler<AsyncResult<Integer>> next) {
        connection.updateWithParams(query, datas, ar -> {
            if (ar.failed()) {
                next.handle(Future.failedFuture(ar.cause()));
                connection.close();
                return;
            }
            UpdateResult result = ar.result();
            if (result.getUpdated() == 0) {
                next.handle(Future.failedFuture(ar.cause()));
                return;
            }
            next.handle(Future.succeededFuture(result.getKeys().getInteger(0)));
        });
    }

    public static void queryWithParams(String query, JsonArray datas, SQLConnection connection, Handler<AsyncResult<List<JsonObject>>> next) {
        connection.queryWithParams(query, datas, ar -> {
            if (ar.failed()) {
                next.handle(Future.failedFuture(ar.cause()));
            } else {
                if (ar.result().getNumRows() >= 1) {
                    next.handle(Future.succeededFuture(ar.result().getRows()));
                } else {
                    next.handle(Future.failedFuture("fdsfdsf"));
                }
            }
        });
    }


	/*

	public ResultSet query(String qeury) {
		try {
			Statement stmt = (Statement) Jdbc.conn.createStatement();
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
			preparedStmt = Jdbc.conn.prepareStatement(query);
			
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
			preparedStmt = Jdbc.conn.prepareStatement(query);
			
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
