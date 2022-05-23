package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SettingActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String NO_OF_TASK_TO_SHOW = "No of Task to show";
    private static final String TAG = SettingActivity.class.getSimpleName();
    public static final String USER_TEAM = "User Team";
    private EditText mUsername;
    private EditText mNoOfTaskToShow;
    private Spinner selectTeamSpinner;
    private Button saveUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewByIdMethod();

        setAdapterToStatesTeamArraySpinner();

        addBackButton();

        saveUserButton.setOnClickListener(view -> saveButtonAction());
    }

    private void saveUser() {
        String username = mUsername.getText().toString();
        String NoOfTaskToShow = mNoOfTaskToShow.getText().toString();
        String userTeam = selectTeamSpinner.getSelectedItem().toString();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(USERNAME, username);
        preferenceEditor.putString(NO_OF_TASK_TO_SHOW, NoOfTaskToShow);
        preferenceEditor.putString(USER_TEAM, userTeam);
        Log.i(TAG, "saveUser: userName is => " + username);
        Log.i(TAG, "saveUser: userTeam is => " + userTeam);
        preferenceEditor.apply();
        backToMainActivity();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public void backToMainActivity() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void setAdapterToStatesTeamArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        List<String> teams = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            teams = SplashActivity.teamsList.stream().map(Team::getName).sorted().collect(Collectors.toList());
        }

        /*
        https://www.codegrepper.com/code-examples/java/android+studio+how+to+fill+spinner
         */
        Spinner spinner = findViewById(R.id.task_team_spinner_setting);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                teams);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void saveButtonAction() {
        if (TextUtils.isEmpty(mUsername.getText())) {
            mUsername.setError("username is required");
        } else {
            saveUser();
            disableTheTextEdit();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }


    private void findViewByIdMethod() {

        mUsername = findViewById(R.id.edit_text_username);
        mNoOfTaskToShow = findViewById(R.id.edit_text_no_of_task);
        saveUserButton = findViewById(R.id.save_user_button);
        selectTeamSpinner = findViewById(R.id.task_team_spinner_setting);

    }

    private void addBackButton() {
        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
        */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void disableTheTextEdit(){

        mUsername.setEnabled(false);
        selectTeamSpinner.setEnabled(false);

    }

    private void enableTheTextEdit(){

        mUsername.setEnabled(true);
        selectTeamSpinner.setEnabled(true);

    }
}