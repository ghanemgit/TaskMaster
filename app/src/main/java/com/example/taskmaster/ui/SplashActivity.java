package com.example.taskmaster.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.taskmaster.Auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.N)
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    public static List<Team> teamsList = new ArrayList<>();
    public static boolean isSignedInBoolean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureAmplify();

        Log.i(TAG, "Is online -> "+isOnline());
        if (isOnline()) {
            onlineFetchTeamsData(this);
            deleteDataStoreContent();
            checkTheSession();
        } else {
            offlineFetchTeamsData(this);
            Toast.makeText(this, "No Internet connection!!", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        }
        splashScreenLaunch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
    }

    public void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSPinpointAnalyticsPlugin(getApplication()));
            Amplify.addPlugin(new AWSPredictionsPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void onlineFetchTeamsData(Context context) {

        Amplify.API.query(
                ModelQuery.list(Team.class),
                teams -> {
                    teamsList.clear();
                    if (teams.hasData()) {
                        for (Team team : teams.getData()) {
                            teamsList.add(team);
                        }
                    }
                    Log.i(TAG, "The team size is ->  " + teamsList.size());
                },
                error -> {
                    Log.e(TAG, error.toString());
                    Toast.makeText(context, "Error in data sync from cloud", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void deleteDataStoreContent(){

        Amplify.DataStore.clear(
                () -> Amplify.DataStore.start(
                        () -> {
                            Log.i(TAG, "DataStore Cleared");
                            Log.i(TAG, "DataStore started");
                        },
                        error -> Log.e(TAG, "Error starting DataStore: ", error)
                ),
                failure -> Log.e(TAG, "Failed to clear DataStore.")
        );
    }

    private void checkTheSession() {

        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i(TAG, result.toString());
                    Log.i(TAG, "checkTheSession: is signed in -> "+result);
                    if (result.isSignedIn()) {
                        isSignedInBoolean = true;
                        navigateToMainActivity();
                    } else {
                        isSignedInBoolean = false;
                        navigateToLoginPage();
                    }
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    public static void offlineFetchTeamsData(Context context) {

        Amplify.DataStore.query(Team.class,
                allTeams -> {
                    teamsList.clear();
                    while (allTeams.hasNext()) {
                        Team team = allTeams.next();
                        Log.i(TAG, "Title: " + team.getName());
                        teamsList.add(team);
                    }
                },
                failure -> {
                    Log.e(TAG, "Query failed.", failure);
                    Toast.makeText(context, "Error in data sync from local", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void splashScreenLaunch() {

        new Handler().postDelayed(() -> {

        }, 2000);
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void navigateToLoginPage() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}