package com.example.taskmaster.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
@RequiresApi(api = Build.VERSION_CODES.N)
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    static List<Task> tasksList = new ArrayList<>();
    static List<Team> teamsList = new ArrayList<>();
    private static String theUserTeamId = "";
    private static String theUserTeamString = "";
    private static boolean isSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureAmplify();
        fetchTaskDataFromAPI();
        fetchTeamsData();
        RecyclerViewHandler();
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
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                // close this activity
                finish();
            }
        }, 1500);

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
                        }
                    }
                },
                error -> Log.e(TAG, error.toString())
        );

    }

    public void fetchTaskDataFromAPI() {

            Amplify.API.query(
                    ModelQuery.list(Task.class),
                    tasks -> {
                        if (tasks.hasData()) {
                            tasksList.clear();
                            for (Task task : tasks.getData()) {
                                tasksList.add(task);
                            }
                            Log.i(TAG, "fetchTaskDataFromAPI: Tasks import Done => "+tasksList.size());
                        }
                    },
                    error -> Log.e(TAG, error.toString())
            );
    }

    private static void filterUserTaskAccordingToTeam(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            List<Team> tempList = teamsList.stream().filter(team -> team.getName().equals(theUserTeamString)).collect(Collectors.toList());
//            theUserTeamId = tempList.get(0).getId();
//        }
//        Amplify.API.query(
//                ModelQuery.list(Task.class, Task.TEAM_TASKS_ID.eq(theUserTeamId)),
//                tasks -> {
//                    if (tasks.hasData()) {
//                        tasksList.clear();
//                        for (Task task : tasks.getData()) {
//                            tasksList.add(task);
//                        }
//                    }
//                },
//                error -> Log.e(TAG, error.toString())
//        );
    }

    private void RecyclerViewHandler() {

        System.out.println("The final size of list is from recycler view =>" + tasksList.size());

        Handler handler = new Handler(Looper.getMainLooper(), msg -> {

            new RecyclerViewActivity(tasksList);

            return true;

        });
    }

}