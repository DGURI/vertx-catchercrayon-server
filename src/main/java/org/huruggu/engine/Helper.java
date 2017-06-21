package org.huruggu.engine;

import io.vertx.core.json.JsonObject;

import java.util.Iterator;
import java.util.Map;


public class Helper {
    public static JsonObject config = ServerEngine.getConfig();
    public static boolean DEBUG = Helper.config.getBoolean("debug");

    public static void printDebug(Object obj) {
        if (Helper.DEBUG) System.out.println(obj);
    }

    public static void printException(String name, Exception e) {
        if (Helper.DEBUG) {
            System.out.println(name + " Exception");
            e.printStackTrace();
		}
	}
	
	public static void threadStatus() {
        if (Helper.DEBUG) {
            Map map = Thread.getAllStackTraces();
            Iterator<Thread> it = map.keySet().iterator();
	        int x = 0;
	        while(it.hasNext()) {
	        	Object obj = it.next();
	        	Thread t = (Thread)obj;
	        	
	        	StackTraceElement[] ste = (StackTraceElement[])map.get(obj);
	        	System.out.println("[" + (++x) + "] name : " + t.getName() + ", group : " + t.getThreadGroup().getName() + ", isDaemon : " + t.isDaemon());
	        	
	        	for (StackTraceElement stackTraceElement : ste) {
					System.out.println(stackTraceElement);
				}
	        	System.out.println();
	        }
		}
	}
}
