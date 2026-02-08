package com.example.learnhub.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

        private static final String PREF_NAME = "user_session";
        private static final String KEY_USER_TYPE = "user_type";
        private static final String KEY_CLASS_CODE = "class_code";
        private static final String KEY_USER_NAME = "user_name";
        private static final String KEY_STD_NAME = "std_name";
        private static final String KEY_USER_EMAIL = "user_email";
        private static final String KEY_USER_PASSWORD = "user_password";
        private SharedPreferences sharedPreferences;

        public UserSession(Context context) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }

        // Save user type
        public void saveUserType(String userType) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_TYPE, userType);
            editor.apply();
        }
        public  void saveClassCode(String classcode){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_CLASS_CODE, classcode);
            editor.apply();
        }
        public void saveUserName(String username){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_NAME, username);
            editor.apply();
        }
    public void saveUserEmail(String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
    public void saveUserPassword(String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_PASSWORD, password);
        editor.apply();
    }
    public void saveStdName(String stdname){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STD_NAME, stdname);
        editor.apply();
    }
    public String getClassCode() {
        return sharedPreferences.getString(KEY_CLASS_CODE, "default"); // default if not set
    }

        // Get user type
        public String getUserType() {
            return sharedPreferences.getString(KEY_USER_TYPE, "default"); // default if not set
        }
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "default"); // default if not set
    }
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "default"); // default if not set
    }
    public String getUserPassword() {
        return sharedPreferences.getString(KEY_USER_PASSWORD, "default"); // default if not set
    }
    public String getStdName() {
        return sharedPreferences.getString(KEY_STD_NAME, "default"); // default if not set
    }

    }




