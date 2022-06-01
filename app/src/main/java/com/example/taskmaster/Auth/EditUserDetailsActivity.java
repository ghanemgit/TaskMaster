package com.example.taskmaster.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;
import com.example.taskmaster.ui.LoadingDialog;
import com.example.taskmaster.ui.SettingActivity;
import com.example.taskmaster.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditUserDetailsActivity extends AppCompatActivity {

    private static final String TAG = EditUserDetailsActivity.class.getSimpleName();
    private EditText editFirstName;
    private EditText editLastNameSignup;
    private Button editBtn;
    private Spinner editSelectTeamSpinner;
    private String firstNameEditString;
    private String lastNameEditString;
    private String selectedItemSpinnerString;
    private String selectedTeamId;
    private String currentFirstName;
    private String currentLastName;
    private String currentTeam;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        findAllViewById();
        initializeAllString();
        setAdapterToStatesTeamArraySpinner();
        setUserInfoByUsingIntentToEnableEdit();
        buttonsAction();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findAllViewById() {
        editFirstName = findViewById(R.id.edit_first_name_box);
        editLastNameSignup = findViewById(R.id.edit_last_name_box);
        editSelectTeamSpinner = findViewById(R.id.edit_task_team_spinner);
        editBtn = findViewById(R.id.edit_button);
        loadingDialog = new LoadingDialog(EditUserDetailsActivity.this);
    }

    private void initializeAllString() {

        currentFirstName = UserInfo.getDefaults(UserInfo.FIRST_NAME,null,this);
        currentLastName = UserInfo.getDefaults(UserInfo.LAST_NAME,null,this);
        currentTeam = UserInfo.getDefaults(UserInfo.USER_TEAM,null,this);
    }

    private void setAdapterToStatesTeamArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        List<String> teams;
        teams = SplashActivity.teamsList.stream().map(Team::getName).sorted().collect(Collectors.toList());

        /*
        https://www.codegrepper.com/code-examples/java/android+studio+how+to+fill+spinner
         */
        editSelectTeamSpinner = findViewById(R.id.edit_task_team_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                teams);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editSelectTeamSpinner.setAdapter(adapter);
    }

    private void setUserInfoByUsingIntentToEnableEdit() {

        editFirstName.setText(currentFirstName);
        editLastNameSignup.setText(currentLastName);
        editSelectTeamSpinner.setSelection(0);
    }

    private void buttonsAction() {

        editBtn.setOnClickListener(view -> {
            getAllAsStrings();
            editButtonAction();
        });
    }

    private void getAllAsStrings() {
        firstNameEditString = editFirstName.getText().toString().trim();
        lastNameEditString = editLastNameSignup.getText().toString().trim();
        selectedItemSpinnerString = editSelectTeamSpinner.getSelectedItem().toString();
        selectedTeamId = SplashActivity.teamsList.stream().filter(team -> Objects.equals(team.getName(),selectedItemSpinnerString )).findFirst().get().getId();
    }

    private void editButtonAction() {

        if (editFirstName.getText().toString().equals(currentFirstName)
                && editLastNameSignup.getText().toString().equals(currentLastName)
                && editSelectTeamSpinner.getSelectedItem().toString().equals(currentTeam)) {

            withoutEditAlert();

        } else if (TextUtils.isEmpty(editFirstName.getText())) {

            editFirstName.setError("Enter a first name");


        } else if (TextUtils.isEmpty(editLastNameSignup.getText())) {

            editLastNameSignup.setError("Enter a last name");

        } else {

            loadingDialog.startLoadingDialog();
            update();

        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }

    }

    private void withoutEditAlert() {
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(EditUserDetailsActivity.this);
        deleteAlert.setTitle("Alert");
        deleteAlert.setMessage("Make At least one Edit");
        /*
        How to add alert to my program
        https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
        */
        deleteAlert.setPositiveButton("ok", (dialogInterface, i) -> {
            onResume();
        });
        deleteAlert.show();
    }

    private void update() {

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), firstNameEditString));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), lastNameEditString));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:user_team"), selectedItemSpinnerString));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:user_team_id"), selectedTeamId));


        Log.i(TAG, "First NAme: "+firstNameEditString);
        Log.i(TAG, "Last NAme: "+lastNameEditString);
        Log.i(TAG, "Team NAme: "+selectedItemSpinnerString);
        Log.i(TAG, "Team ID: "+selectedTeamId);




        Amplify.Auth.updateUserAttributes(
                attributes,
                result -> {
                    Log.i(TAG, "Result: " + result);
                    sendInfoToUserInfoClass();
                    runOnUiThread(this::navigateToSettingsActivity);
                },
                error -> {
                    Log.e(TAG, "update failed", error);
                    runOnUiThread(() -> {
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(this, "Edit can't complete something went wrong", Toast.LENGTH_SHORT).show();
                        onResume();
                    });
                }
        );
    }

    private void sendInfoToUserInfoClass() {

        UserInfo.firstName = firstNameEditString;
        UserInfo.lastName = lastNameEditString;
        UserInfo.userTeam = selectedItemSpinnerString;
        UserInfo.userTeamId = selectedTeamId;
        UserInfo.saveOtherUserInfoToSharedPreferences(this);

    }

    private void navigateToSettingsActivity() {

        startActivity(new Intent(this, SettingActivity.class));
        loadingDialog.dismissLoadingDialog();
        finish();
    }

}