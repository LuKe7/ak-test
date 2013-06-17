package com.example.brightcovetest;

import java.util.HashMap;

public class LoginManager {
	private static HashMap<String, String> users = new HashMap<String, String>();
	private static String currentUser;
	private LoginManager() {
		// TODO Auto-generated constructor stub
	}

	public synchronized static boolean register(String username, String password) {
		if (!users.containsKey(username)) {
			users.put(username, password);
			return true;
		}
		return false;
	}

	public synchronized static boolean verifyUser(String username, String password) {
		return users.containsKey(username) && users.get(username).equals(password);
	}

	public synchronized static boolean login(String username, String password) {
		if (verifyUser(username, password)) {
			setCurrentUser(username);
			return true;
		}
		return false;
	}
	
	public synchronized static String getCurrentUser(){
		return currentUser;
	}

	private synchronized static void setCurrentUser(String user) {
		currentUser = user;
	}
	
	public synchronized static void signOut(){
		currentUser = null;
	}
}
