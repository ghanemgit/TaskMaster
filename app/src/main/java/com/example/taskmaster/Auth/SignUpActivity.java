package com.example.taskmaster.Auth;


import android.annotation.SuppressLint;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.example.taskmaster.ui.LoadingDialog;
import com.example.taskmaster.ui.MainActivity;
import com.example.taskmaster.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint("StaticFieldLeak")
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private static EditText firstNameSignup;
    private static EditText lastNameSignup;
    private static EditText emailSignup;
    private EditText passwordSignup;
    private EditText confirmPassword;
    private Button signupBtn;
    private Spinner selectTeamSpinner;
    private String firstNameSignupString;
    private String lastNameSignupString;
    private String emailSignupString;
    private String passwordSignupString;
    private String confirmPasswordSignupString;
    private String selectedItemSpinnerString;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findAllViewById();
        setAdapterToStatesTeamArraySpinner();
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findAllViewById() {
        firstNameSignup = findViewById(R.id.first_name_box);
        lastNameSignup = findViewById(R.id.last_name_box);
        emailSignup = findViewById(R.id.signup_email_box);
        passwordSignup = findViewById(R.id.signup_password_box);
        confirmPassword = findViewById(R.id.confirm_password_box);
        signupBtn = findViewById(R.id.signup_button);
        loadingDialog = new LoadingDialog(SignUpActivity.this);
    }

    private void getAllAsStrings() {
        firstNameSignupString = firstNameSignup.getText().toString().trim();
        lastNameSignupString = lastNameSignup.getText().toString().trim();
        emailSignupString = emailSignup.getText().toString().trim();
        passwordSignupString = passwordSignup.getText().toString().trim();
        confirmPasswordSignupString = confirmPassword.getText().toString().trim();
        selectedItemSpinnerString = selectTeamSpinner.getSelectedItem().toString();
    }


    private void signupButtonAction() {

        if (TextUtils.isEmpty(emailSignupString) || emailSignupString.contains(" ")) {
            emailSignup.setError("Email is required");

        } else if (!passwordSignupString.equals(confirmPasswordSignupString)) {

            passwordSignup.setError("Password is Mismatched");
            confirmPassword.setError("Password is Mismatched");
        }else if (passwordSignupString.length() < 8) {

            passwordSignup.setError("Password too short");
            confirmPassword.setError("Password too short");
        }  else {
            loadingDialog.startLoadingDialog();
            signup();
        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }

    }

    private void signup() {

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), emailSignupString));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), firstNameSignupString));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), lastNameSignupString));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:user_team"), selectedItemSpinnerString));



        Amplify.Auth.signUp(
                emailSignupString,
                passwordSignupString,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                result -> {
                    Log.i(TAG, "Result: " + result);
                    runOnUiThread(() -> {
                        savePasswordSharedPreferences();
                        navigateToVerificationActivity();
                    });

                },
                error -> {
                    Log.e(TAG, "Sign up failed", error);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Signup can't complete something went wrong", Toast.LENGTH_SHORT).show();
                        onResume();
                    });

                }
        );
    }

    private void navigateToVerificationActivity() {
        Intent intent = new Intent(this, VerificationCodeActivity.class);
        intent.putExtra("emailFromSignUp", emailSignupString);
        loadingDialog.dismissLoadingDialog();
        startActivity(intent);
        finish();
    }

    private void buttonsAction() {

        signupBtn.setOnClickListener(view -> {
            getAllAsStrings();
            signupButtonAction();
        });
    }


    private void savePasswordSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(emailSignupString, passwordSignupString);
        preferenceEditor.apply();
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
        selectTeamSpinner = findViewById(R.id.signup_task_team_spinner_setting);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                teams);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectTeamSpinner.setAdapter(adapter);
    }
}