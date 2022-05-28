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
import com.example.taskmaster.Auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.N)
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    public static List<Team> teamsList = new ArrayList<>();
    public static List<AuthUserAttribute> userAttributes = new ArrayList<>();

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

                //This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                // close this activity
                finish();
            }
        }, 1000);
    }

    private void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
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

    private void checkTheSession(){
        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i(TAG, result.toString());
                    if (result.isSignedIn()){
                        fetchCurrentUserAttributes();
                        navigateToMainActivity();
                    }else {
                        navigateToLoginPage();
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
                        Log.i(TAG, "fetchTaskDataFromAPI: Tasks import Done => " + MainActivity.tasksList.size());
                    }
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    private void fetchCurrentUserAttributes(){

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i(TAG, "User attributes = " + attributes);
                    userAttributes = attributes;
                },
                error -> Log.e(TAG, "Failed to fetch user attributes.", error)
        );
    }

    private void navigateToLoginPage(){
        startActivity(new Intent(this,LoginActivity.class));
    }

    private void navigateToMainActivity(){
        startActivity(new Intent(this,MainActivity.class));
    }
}