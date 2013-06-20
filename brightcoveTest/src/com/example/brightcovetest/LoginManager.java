package com.example.brightcovetest;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

public class LoginManager {
    private static HashMap<String, String> userMap = new HashMap<String, String>();

    private static final String SETTINGS_FILENAME = "settings";

    private static String currentUser;

    private LoginManager() {
        // TODO Auto-generated constructor stub
    }

    public synchronized static boolean register(String username, String password) {
        User user = lookupUser(username);
        if (user.username == null || user.username.isEmpty()) {
            userMap.put(username, password);

            appendUserToFile(new User(username, password));
            return true;

        }
        return false;
    }

    private synchronized static void appendUserToFile(User user) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(TestApplicaton.getAppContext().getFilesDir(), "users"), true));
            writer.write(user.username + " " + user.password);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static boolean verifyUser(String username, String password) {
        Log.d("", "SIGNIN 1");
        User user = lookupUser(username);
        if (user.username == null || user.username.isEmpty()) {
            Log.d("", "SIGNIN 2");
            return false;
        }
        return user.password.equals(password);
    }

    public synchronized static boolean login(String username, String password) {
        if (verifyUser(username, password)) {
            setCurrentUser(username);
            return true;
        }
        return false;
    }

    public synchronized static String getCurrentUser() {
        return currentUser;
    }

    private synchronized static void setCurrentUser(String user) {
        currentUser = user;
    }

    public synchronized static void signOut() {
        currentUser = null;
    }


    private synchronized static User lookupUser(String username) {
        if (userMap.isEmpty()) {
            Log.d("", "SIGNIN importing");
            importUsers();
        }

        if (userMap.containsKey(username)) {
            return new User(username, userMap.get(username));
        }
        return new User();

    }

    private synchronized static void importUsers() {
        String user;
        String password;
        File file = new File(TestApplicaton.getAppContext().getFilesDir(), "users");
        try {
            Log.d("", "SIGNIN 3");
            Scanner scanner = new Scanner(file);
            userMap.clear();
            while (scanner.hasNext()) {
                user = scanner.next();
                Log.d("", "SIGNIN read:" + user);
                password = scanner.next();
                userMap.put(user, password);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }


    }

    private static class User {
        String username;
        String password;

        public User() {
        }

        public User(String u, String p) {
            username = u;
            password = p;
        }
    }
}
