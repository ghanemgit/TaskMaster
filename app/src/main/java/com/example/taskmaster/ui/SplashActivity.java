package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    static List<Task> tasksList = new ArrayList<>();
    static List<Team> teamsList = new ArrayList<>();
    private String theUserTeamId = "";
    private String theUserTeamString = "";
    private static boolean isSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        configureAmplify();
        fetchTeamsData();
        splashScreenLaunch();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: called");
    }

    private void splashScreenLaunch() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;


                //This method will be executed once the timer is over
                // Start your app main activity
                if (isSet) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, SettingActivity.class);
                }

                startActivity(intent);
                // close this activity
                finish();
            }
        }, 2000);

    }

    private void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

    private void fetchTeamsData() {
        Amplify.API.query(
                ModelQuery.list(Team.class),
                teams -> {
                    if (teams.hasData()) {
                        for (Team team : teams.getData()) {
                            teamsList.add(team);

                            Log.i(TAG, "<@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@>");
                            Log.i(TAG, "The Team name is => " + team.getName());
                            Log.i(TAG, "The Team tasks is => " + team.getTasks().toString());
                            Log.i(TAG, "The size of list is => " + teamsList.size());
                            Log.i(TAG, "The team id is => " + team.getId());
                        }
                    }

                    isSet = isTheUserSetTheTeamName(teamsList);
                    if (isSet)
                        fetchDataFromAPI();
                },
                error -> Log.e(TAG, error.toString())

        );

    }

    private void fetchDataFromAPI() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<Team> tempList = teamsList.stream().filter(team -> team.getName().equals(theUserTeamString)).collect(Collectors.toList());
            theUserTeamId = tempList.get(0).getId();
        }

        Amplify.API.query(
                ModelQuery.list(Task.class, Task.TEAM_TASKS_ID.eq(theUserTeamId)),
                tasks -> {
                    if (tasks.hasData()) {
                        tasksList.clear();
                        for (Task task : tasks.getData()) {
                            tasksList.add(task);

                            Log.i(TAG, "<@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@>");
                            Log.i(TAG, "The task title is => " + task.getTitle());
                            Log.i(TAG, "The task description is => " + task.getDescription());
                            Log.i(TAG, "The task status is => " + task.getStatus());
                            Log.i(TAG, "The task team id is => " + task.getTeamTasksId());
                            Log.i(TAG, "The size of list is => " + tasksList.size());
                        }
                    }
                },
                error -> Log.e(TAG, error.toString())
        );


    }

    private boolean isTheUserSetTheTeamName(List<Team> teamsListToCheck) {

        theUserTeamString = SettingActivity.getDefaults(SettingActivity.USER_TEAM, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return teamsListToCheck.stream().anyMatch(team -> team.getName().equals(theUserTeamString));
        } else return false;


    }
}