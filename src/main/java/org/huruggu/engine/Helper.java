package org.huruggu.engine;

import java.util.Iterator;
import java.util.Map;


public class Helper {
	
	public static void printDebug(Object obj) {
		if(Config.DEBUG) System.out.println(obj);
	}
	
	public static void printException(String name, Exception e) {
		if(Config.DEBUG) {
			System.out.println(name + " Exception");
			e.printStackTrace();
		}
	}
	
	public static void threadStatus() {
		if(Config.DEBUG) {
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
