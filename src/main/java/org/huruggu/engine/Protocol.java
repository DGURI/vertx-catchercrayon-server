/*
 * 통신규약 엔진
 * 
 * 클라이언트들과  
 * 
 */
package org.huruggu.engine;

import java.io.Serializable;


public class Protocol implements Serializable {
	private String route = null;
	private String json = null;
	
	public static final String LOGIN 		= "auth/login"; // login route
	public static final String LOGOUT		= "auth/logout"; // logout (socket disconnect) route
	public static final String RENEW 		= "auth/renew"; // login route
	public static final String REGISTER 	= "auth/register"; // register route
	public static final String ISDUPLICATE	= "auth/duplicate"; // identity duplicate check route
	public static final String DISCONNECT	= "diconnect"; // identity duplicate check route
	
	public static final String GAME_ANOTHER_PLAYER_JOIN 	= "game/another_player_join";
	public static final String GAME_JOIN 					= "game/join";
	public static final String GAME_ANOTHER_PLAYER_LEAVE 	= "game/another_player_leave";
	public static final String GAME_PLAY					= "game/play";
	public static final String GAME_TURN					= "game/turn";
	public static final String GAME_ROUND					= "game/round";
	
	public static final String GAME_CHAT					= "game/chat";
	public static final String GAME_SUBMIT_ANSWER			= "game/submit_answer";
	
	public static final String DRAWING	 	= "canvas/drawing"; // canvas - draw line event route
	public static final String CANVASCLEAR	= "canvas/drawReset"; // canvas - clear
	
	public static final int MAX_USER = 8;
	public static final int MAX_ROUND = 6;

	
	public String getRoute() {
		return this.route;
	}
	
	public void setRoute(String route) {
		this.route = route;
	}
	
	public String getJSON() {
		return this.json;
	}
	
	public void setJSON(String json) {
		this.json = json;
	}


	
}