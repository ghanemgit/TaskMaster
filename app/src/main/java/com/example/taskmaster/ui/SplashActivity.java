package com.example.taskmaster.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.taskmaster.Auth.LoginActivity;
import com.example.taskmaster.data.UserInfo;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    public static List<AuthUserAttribute> userAttributes = new ArrayList<>();
    public static List<Team> teamsList = new ArrayList<>();
    private boolean isSignedInBoolean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureAmplify();
        checkTheSession();
        fetchTeamsData();
        fetchTasksDataFromAPI();
        splashScreenLaunch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
    }


    private void splashScreenLaunch() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSignedInBoolean)
                    navigateToMainActivity();
                else
                    navigateToLoginPage();
            }
        }, 1000);
    }

    private void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }
    }

    private void fetchTeamsData() {

        Amplify.API.query(
                ModelQuery.list(Team.class),
                teams -> {
                    if (teams.hasData()) {
                        for (Team team : teams.getData()) {
                            teamsList.add(team);
                        }
                    }
                },
                error -> Log.e(TAG, error.toString())
        );


    }

    private void checkTheSession() {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i(TAG, result.toString());
                    if (result.isSignedIn()) {
                        fetchCurrentUserAttributes();
                        isSignedInBoolean = true;
                    } else {
                        isSignedInBoolean = false;
                    }
                },
                error -> Log.e(TAG, error.toString())
        );

    }

    private void fetchTasksDataFromAPI() {

        Amplify.API.query(
                ModelQuery.list(Task.class),
                tasks -> {
                    MainActivity.tasksList.clear();
                    if (tasks.hasData()) {
                        for (Task task : tasks.getData()) {
                            MainActivity.tasksList.add(task);
                        }
                    }
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    private void fetchCurrentUserAttributes() {

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i(TAG, "User attributes = " + attributes);
                    userAttributes = attributes;
                    sendInfoToUserInfoClass();
                },
                error -> Log.e(TAG, "Failed to fetch user attributes.", error)
        );
    }

    private void navigateToLoginPage() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void sendInfoToUserInfoClass(){


        UserInfo.firstName = userAttributes.get(3).getValue();
        UserInfo.lastName = userAttributes.get(4).getValue();
        UserInfo.email = userAttributes.get(5).getValue();
        UserInfo.userTeam = userAttributes.get(0).getValue();
        UserInfo.saveOtherUserInfoToSharedPreferences(this);
    }


}