package com.example.taskmaster.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.taskmaster.Adapter.CustomListRecyclerViewAdapter;
import com.example.taskmaster.Adapter.ViewPager2Adapter;
import com.example.taskmaster.Auth.LoginActivity;
import com.example.taskmaster.Fragments.CompletedTaskFragment;
import com.example.taskmaster.Fragments.InProgressTaskFragment;
import com.example.taskmaster.Fragments.NewTaskFragment;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint({"NotifyDataSetChanged","MissingPermission"})
public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView usernameWelcoming;

    private FloatingActionButton expandableFloatingActionButton, floatAddTaskButton, interstitialAdFloatingButton, rewardedAdFloatingButton;

    private SwipeRefreshLayout swipeContainer;

    public static List<Task> tasksList = new ArrayList<>();

    private String statusSelected;

    private LoadingDialog loadingDialog;

    private CustomListRecyclerViewAdapter customListRecyclerViewAdapter;

    private RecyclerView taskRecyclerView;

    private Spinner sortSpinner;

    private AdView mAdView;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;

    private boolean isClicked = false;

    private InterstitialAd mInterstitialAd;

    private RewardedAd mRewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        findAllViewsById();

        loadAllAds();

        onRefreshPullListener();

        configureAmplify();

        recordEvents();

        if (isOnline()) onlineFetchTasksData();

        else offlineFetchTasksData();

        showUserNameOrTeam();

        //set adapter for filter tasks spinner
        setAdapterToStatesTaskArraySpinner();

        taskFilterSpinner();

        setOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: called");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnline()) loadAllAds();
        sortSpinner.setSelection(0);
        showUserNameOrTeam();
        Log.i(TAG, "onResume: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void findAllViewsById() {
        swipeContainer = findViewById(R.id.swipe_refresh_layout);
        usernameWelcoming = findViewById(R.id.username_welcoming);
        /*
        https://developer.android.com/guide/topics/ui/floating-action-button
         */
        expandableFloatingActionButton = findViewById(R.id.expandable_floating_button);
        floatAddTaskButton = findViewById(R.id.add_task_button_floating);
        interstitialAdFloatingButton = findViewById(R.id.interstitial_ad_floating_button);
        rewardedAdFloatingButton = findViewById(R.id.rewarded_ad_floating_button);

        loadingDialog = new LoadingDialog(MainActivity.this);
        taskRecyclerView = findViewById(R.id.recycler_view);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anm);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anm);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anm);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_buttom_anm);
    }

    private void loadAllAds(){

        loadABannerAdAd();

        showBannerAd();

        loadAInterstitialAd();

        loadRewardedAd();
    }

    private void loadABannerAdAd() {

        //This code for banner Ad
        mAdView = findViewById(R.id.ad_view);
        AdRequest adBannerRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adBannerRequest);
        //This code for banner ad
    }

    private void showBannerAd() {

        ////////This code for banner Ad\\\\\\\\\
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.i(TAG, "onAdFailedToLoad: Ad failed to load");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        ////////This code for banner Ad\\\\\\\\\
    }

    private void loadAInterstitialAd() {

        //This code for Interstitial Ad
        AdRequest adInterstitialRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adInterstitialRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        //you have to put the full screen content call back inside on ad load method to avoid null pointer exception
                        interstitialFullScreenContentCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, "On failed to load -> " + mInterstitialAd + " =>" + loadAdError.getMessage());
                        mInterstitialAd = null;
                    }

                });
        Log.i(TAG, "On failed to load -> " + mInterstitialAd);

        //This code for Interstitial Ad
    }

    private void interstitialFullScreenContentCallback() {
        ////////This code for Interstitial Ad\\\\\\
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }
        });
        ////////This code for Interstitial Ad\\\\\\
    }

    private void showInterstitialAd() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    private void loadRewardedAd(){

        //This code for Rewarded Ad
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        MainActivity.this.mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
                        rewardedFullScreenContentCallback();
                    }
                });
        //This code for Rewarded Ad

    }

    private void rewardedFullScreenContentCallback(){

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.");
                earnPointsAlertDialog();
                mRewardedAd = null;
            }
        });
    }

    private void showRewardedAd(){

        if (mRewardedAd != null) {
            Activity activityContext = MainActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    private void earnPointsAlertDialog() {
        /*
        https://stackoverflow.com/questions/33437398/how-to-change-textcolor-in-alertdialog
        how to change the text color in alert dialog
        */
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("You earned 50 points\n\nThank you for support us");
        alert.setTitle(Html.fromHtml("<font color='#00FFAB'>Congratulations</font>"));


        alert.setPositiveButton(Html.fromHtml("<font color='#00FFAB'>ok</font>"), (dialog, whichButton) -> {
            UserInfo.userPoints+=50;
            onResume();
        });

        alert.show();
    }

    private void onRefreshPullListener() {

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isOnline()) {
                    onlineFetchTasksData();
                    loadAllAds();
                }

                else offlineFetchTasksData();

                swipeContainer.setRefreshing(false);
            }
        });
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

    public void recordEvents() {

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("Task Master Opened")
                .addProperty("Successful", true)
                .addProperty("ProcessDuration", 792)
                .build();

        Amplify.Analytics.recordEvent(event);

    }

    //Set up the username details to show it in the home screen
    @SuppressLint("SetTextI18n")
    public void showUserNameOrTeam() {

        usernameWelcoming.setText(UserInfo.getDefaults(UserInfo.FIRST_NAME, "Guest", this) + " " + UserInfo.getDefaults(UserInfo.LAST_NAME, "", this) + " Tasks");

        usernameWelcoming.setText(UserInfo.getDefaults(UserInfo.USER_TEAM, UserInfo.getDefaults(UserInfo.FIRST_NAME, "Guest", this), this) + " Tasks");
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void onlineFetchTasksData() {

        Amplify.API.query(
                ModelQuery.list(Task.class, Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this))),
                tasks -> {
                    tasksList.clear();
                    if (tasks.hasData()) {
                        for (Task task : tasks.getData()) {
                            tasksList.add(task);
                            Log.i(TAG, "The tasks title -> " + task.getTitle());
                        }
                        runOnUiThread(() -> {
                            getTasksListToHomePage(tasksList);
                        });
                    }


                    Log.i(TAG, "The tasks list from online fetch data method is -> " + tasksList.size());

                },
                error -> {
                    Log.e(TAG, error.toString());
                    Toast.makeText(this, "Error in data sync from cloud", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void offlineFetchTasksData() {

        Amplify.DataStore.query(Task.class, Where.matches(Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this))),
                allTasks -> {
                    tasksList.clear();
                    while (allTasks.hasNext()) {
                        Task task = allTasks.next();
                        Log.i(TAG, "Title: " + task.getTitle());
                        tasksList.add(task);
                    }
                    runOnUiThread(() -> {
                        getTasksListToHomePage(tasksList);
                    });
                },
                failure -> {
                    Log.e(TAG, "Query failed.", failure);
                    Toast.makeText(this, "Error in data sync from local", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void filterTaskAccordingToStatus(String str) {

        if (isOnline()) {
            Amplify.API.query(
                    ModelQuery.list(Task.class, Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this)).and(Task.STATUS.eq(str))),
                    tasks -> {
                        tasksList.clear();
                        if (tasks.hasData()) {
                            for (Task task : tasks.getData()) {
                                tasksList.add(task);
                            }
                        }
                        runOnUiThread(() -> {
                            customListRecyclerViewAdapter.notifyDataSetChanged();
                        });
                    },
                    error -> {
                        Log.e(TAG, error.toString());
                        Toast.makeText(this, "Error in data sync from cloud", Toast.LENGTH_SHORT).show();
                    }
            );
        } else {
            Amplify.DataStore.query(Task.class, Where.matches(
                            Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this))
                                    .and(Task.STATUS.eq(str))),
                    allTasks -> {
                        tasksList.clear();
                        while (allTasks.hasNext()) {
                            Task task = allTasks.next();
                            Log.i(TAG, "Title: " + task.getTitle());
                            tasksList.add(task);
                        }
                        runOnUiThread(() -> {
                            customListRecyclerViewAdapter.notifyDataSetChanged();
                        });
                    },
                    failure -> {
                        Log.e(TAG, "Query failed.", failure);
                        Toast.makeText(this, "Error in data sync from local", Toast.LENGTH_SHORT).show();
                    }
            );
        }
    }

    private void setAdapterToStatesTaskArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        Spinner spinner = findViewById(R.id.task_states_filter_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_states_filter_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void taskFilterSpinner() {

        /*
         * https://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item
         * And as mr.Json tell us in the lecture
         * This spinner is responsible to filter the task for user by states
         */
        sortSpinner = findViewById(R.id.task_states_filter_spinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(i == 0)) {
                    statusSelected = adapterView.getItemAtPosition(i).toString();
                    filterTaskAccordingToStatus(statusSelected);
                    /*
                     * https://stackoverflow.com/questions/3053761/reload-activity-in-android
                     * how to refresh the activity
                     */
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void getTasksListToHomePage(List<Task> tasksListParam) {

        Log.i(TAG, "The tasks list from get task to home page method is -> " + tasksListParam.size());

        customListRecyclerViewAdapter = new CustomListRecyclerViewAdapter(tasksListParam, position -> {
            Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
            intent.putExtra("Position", tasksListParam.get(position).getId());
            startActivity(intent);
        });

        customListRecyclerViewAdapter.notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setHasFixedSize(true);
        taskRecyclerView.setAdapter(customListRecyclerViewAdapter);
    }

    //Set what happens when click on item from the overflow menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                navigateToSetting();
                return true;
            case R.id.action_copyright:
                Toast.makeText(this, "Copyright 2022", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sign_out:
                loadingDialog.startLoadingDialog();
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        /*
        https://stackoverflow.com/questions/3519277/how-to-change-the-text-color-of-menu-item-in-android
        how to change an item color in overflow menu
        */
        int positionOfMenuItem = 2; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Sign out");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    private void signOut() {

        Amplify.Auth.signOut(
                () -> {
                    Log.i(TAG, "Signed out successfully");
                    navigateToLoginPage();
                },
                error -> {
                    Log.e(TAG, error.toString());
                    loadingDialog.dismissLoadingDialog();
                    onResume();
                }
        );
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        loadingDialog.dismissLoadingDialog();
        finish();
    }

    private void setOnClickListeners() {
        //Floating add task button in main activity
        expandableFloatingActionButton.setOnClickListener(view -> {
            setVisibility(isClicked);
            setAnimation(isClicked);
            if (!isClicked) isClicked = true;
            else isClicked = false;
        });
        floatAddTaskButton.setOnClickListener(view -> {
           navigateToAddTaskPage();
        });
        interstitialAdFloatingButton.setOnClickListener(view -> {
            showInterstitialAd();
        });
        rewardedAdFloatingButton.setOnClickListener(view -> {
            showRewardedAd();
        });
    }

    private void setVisibility(boolean clicked) {

        if (!clicked) {
            floatAddTaskButton.setVisibility(View.VISIBLE);
            interstitialAdFloatingButton.setVisibility(View.VISIBLE);
            rewardedAdFloatingButton.setVisibility(View.VISIBLE);

            floatAddTaskButton.setClickable(true);
            interstitialAdFloatingButton.setClickable(true);
            rewardedAdFloatingButton.setClickable(true);
        } else {
            floatAddTaskButton.setVisibility(View.GONE);
            interstitialAdFloatingButton.setVisibility(View.GONE);
            rewardedAdFloatingButton.setVisibility(View.GONE);

            floatAddTaskButton.setClickable(false);
            interstitialAdFloatingButton.setClickable(false);
            rewardedAdFloatingButton.setClickable(false);
        }
    }

    private void setAnimation(boolean clicked) {

        if (!clicked) {
            floatAddTaskButton.startAnimation(fromBottom);
            interstitialAdFloatingButton.startAnimation(fromBottom);
            rewardedAdFloatingButton.startAnimation(fromBottom);
            expandableFloatingActionButton.startAnimation(rotateOpen);

        } else {
            floatAddTaskButton.startAnimation(toBottom);
            interstitialAdFloatingButton.startAnimation(toBottom);
            rewardedAdFloatingButton.startAnimation(toBottom);
            expandableFloatingActionButton.startAnimation(rotateClose);
        }
    }


    private void navigateToAddTaskPage() {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(MainActivity.class.getSimpleName(), MainActivity.class.getSimpleName());
        startActivity(intent);
    }

    private void navigateToSetting() {
        overridePendingTransition(0, 0);
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("tasksListSize", tasksList.size());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}