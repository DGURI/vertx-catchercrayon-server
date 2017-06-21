package org.huruggu.engine;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Models {

    public Models instance = this;
    public Integer counts;
    private JsonObject datas;
    private ArrayList<String[]> wheres;
    private String table = this.getClass().getSimpleName().toLowerCase();
    private String query = null;
    private String orderby = null;
    private String limit = null;


    public Models() {
        this.datas = new JsonObject();
        this.wheres = new ArrayList<String[]>();
    }

    public Models(JsonObject jsonObject) {

    }

    public <T> Models setData(String name, T data) {
        ArrayList<T> datas = new ArrayList<T>();
        datas.add(data);
        this.datas.put(name, datas);
        return this;
    }

    public Models where(String key, String data) {
        String[] where = {key, "and", data};

        this.wheres.add(where);

        return this;
    }

    public Models orWhere(String key, String data) {
        String[] where = {key, "or", data};

        this.wheres.add(where);

        return this;
    }

    public Models select(String select) {
        this.query = "SELECT " + select + " FROM " + this.table;

        return this;
    }

    public Models orderBy(String orderby) {
        this.orderby = " order by " + orderby;

        return this;
    }

    public Models notIn(String column, ArrayList<Integer> datas) {
        String notInQuery = column + " NOT IN (";
        String notInStr = "";
        for (int data : datas) {
            if (notInStr != "") {
                notInStr += ", " + data;
            } else {
                notInStr += data;
            }
        }
        notInQuery += notInStr;
        notInQuery += ")";
        this.where(notInQuery, null);

        return this;
    }

    public <T> void get(int limit, Handler<AsyncResult<List<T>>> aHandler) {
        if (this.query == null)
            this.query = "SELECT * FROM " + this.table;

        JsonArray datas_ = new JsonArray();

        String whereStr = "";
        if (wheres.size() > 0) {
            for (String[] where : this.wheres) {
                String key = where[0];
                String data = where[2];
                if (whereStr != "") {
                    if (data == null) {
                        if (where[1] == "and") whereStr += " and " + key;
                        if (where[1] == "or") whereStr += " or " + key;
                    } else {
                        if (where[1] == "and") whereStr += " and " + key + " = ?";
                        if (where[1] == "or") whereStr += " or " + key + " = ?";
                    }
                } else {
                    if (data == null) {
                        whereStr += " " + key;
                    } else {
                        whereStr += " " + key + " = ?";
                    }
                }
                datas_.add(data);
            }
            this.query += " WHERE" + whereStr;
        }

        if (this.orderby != null) {
            this.query += this.orderby;
        }
        if (this.limit != null) {
            this.query += this.limit;
        }

        System.out.println(this.query);

        DB.getSQLClient().getConnection((AsyncResult<SQLConnection> ar) -> {
            SQLConnection connection = ar.result();
            DB.queryWithParams(this.query, datas_, connection, (AsyncResult<List<JsonObject>> result) -> {
                if (result.failed()) {
                    aHandler.handle(Future.failedFuture("fdsfds"));
                    return;
                }
                List<T> lists = new ArrayList<>();
                try {
                    for (JsonObject jsonObject : result.result()) {
                        T obj = (T) instance.getClass().newInstance();
                        for (Field field : obj.getClass().getFields()) {
                            field.set(obj, jsonObject.getValue(field.getName()));
                        }
                        lists.add(obj);
                    }
                    aHandler.handle(Future.succeededFuture(lists));
                } catch (InstantiationException e) {
                    aHandler.handle(Future.failedFuture(e));
                } catch (IllegalAccessException e) {
                    aHandler.handle(Future.failedFuture(e));
                }
                connection.close();
            });
        });
        return;
    }

    public <T> void get(Handler<AsyncResult<T>> next) {

    }

    public void insert(Handler<AsyncResult<Integer>> aHandler) {
        this.query = "INSERT INTO " + this.table + "(";
        String keyStr = "";
        String dataStr = "";
        JsonArray datas = new JsonArray();

        for (Field field : this.getClass().getFields()) {
            Object object = null;
            try {
                object = field.get(this);
                String key = field.getName();
                if (object == null || key.equals("instance") || key.equals("counts")) continue;
                if (keyStr != "") {
                    keyStr += ", " + key;
                    dataStr += ", ?";
                } else {
                    keyStr += key;
                    dataStr += "?";
                }
                datas.add(object);
            } catch (IllegalAccessException e) {
                aHandler.handle(Future.failedFuture(""));
                return;
            }
        }
        this.query += keyStr + ") VALUES (" + dataStr + ")";
        DB.getSQLClient().getConnection(ar -> {
            SQLConnection connection = ar.result();
            DB.updateWithParams(this.query, datas, connection, (AsyncResult<Integer> r) -> {
                if (r.succeeded()) {
                    aHandler.handle(Future.succeededFuture(r.result()));
                } else {
                    aHandler.handle(Future.failedFuture(r.cause()));
                }
                connection.close();
            });
        });
        System.out.println(this.query);
        return;
    }

    public void count(Handler<AsyncResult<Integer>> aHandler) {
        this.select("count(*) as counts").get(0, result -> {

        });
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        try {
            System.out.println(this.getClass().getName());
            for (Field field : this.getClass().getFields()) {
                Object object = null;
                object = field.get(this);
                String key = field.getName();
                if (object == null || key.equals("instance") || key.equals("counts")) continue;
                jsonObject.put(key, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void toObject(JsonObject jsonObject) {
        try {
            System.out.println(this.getClass().getName());
            for (Field field : this.getClass().getFields()) {
                String key = field.getName();
                if (key.equals("instance") || key.equals("counts")) continue;
                if(field.getType().getSimpleName().equals("String")) {
                    field.set(this, jsonObject.getValue(field.getName()).toString().trim());
                } else {
                    field.set(this, jsonObject.getValue(field.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//		try {
//			System.out.println(query);
//
//			//ResultSet rs = db.prepareQuery(query, datas);
//			int cntColumn = rs.getMetaData().getColumnCount();
//			while(rs.next()) {
//				HashMap<String, String> dataColumn = new HashMap<String, String>();
//				for(int i = 0; i < cntColumn; i++) {
//					String column = rs.getMetaData().getColumnName(i+1);
//					String value = rs.getString(i+1);
//					dataColumn.put(column, value);
//				}
//				dataRows.add(dataColumn);
//			}
//			return dataRows;
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public int count() {
//		ArrayList<HashMap<String, String>> result = this.select("count(*) as counts").get();
//		return Integer.parseInt(result.get(0).get("counts"));
//	}
//
//	public int insert() {
//		DB db = new DB();
//
//		this.query = "INSERT INTO " + table + "(";
//		String keyStr = "";
//		String dataStr = "";
//		ArrayList datas = new ArrayList();
//
//		for(Entry<String, ArrayList> entry : this.datas.entrySet()) {
//		    String key = entry.getKey();
//		    if(keyStr != "") {
//		    	keyStr += ", " + key;
//		    	dataStr += ", ?";
//		    } else {
//		    	keyStr += key;
//		    	dataStr += "?";
//		    }
//		    datas.add(entry.getValue().get(0));
//		}
//		query += keyStr + ") VALUES (" + dataStr + ")";
//
//		System.out.println(query);
//
//		try {
//			return db.prepareQueryU(query, datas);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
}
