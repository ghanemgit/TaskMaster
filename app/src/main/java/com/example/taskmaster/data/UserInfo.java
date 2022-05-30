package com.example.taskmaster.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;

import com.amplifyframework.auth.AuthUserAttribute;
import com.example.taskmaster.Auth.SignUpActivity;
import com.example.taskmaster.ui.SettingActivity;
import com.example.taskmaster.ui.SplashActivity;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UserInfo {

    public static String email;
    public static String firstName;
    public static String lastName;
    public static String userTeam;
    public static String password;

    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first name";
    public static final String USER_TEAM = "User Team";
    public static final String PASSWORD = "Password";
    public static final String LAST_NAME = "last name";

    /*
     * user team index 0
     * username index 1
     * is verified index 2
     * first name index 3
     * last name index 4
     * email index 5
     */


    public static void saveOtherUserInfoToSharedPreferences(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(FIRST_NAME, firstName);
        preferenceEditor.apply();
        preferenceEditor.putString(LAST_NAME, lastName);
        preferenceEditor.apply();
        preferenceEditor.putString(EMAIL, email);
        preferenceEditor.apply();
        preferenceEditor.putString(USER_TEAM, userTeam);
        preferenceEditor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}