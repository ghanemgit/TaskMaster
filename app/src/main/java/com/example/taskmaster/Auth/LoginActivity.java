package com.example.taskmaster.Auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;
import com.example.taskmaster.ui.LoadingDialog;
import com.example.taskmaster.ui.MainActivity;
import com.example.taskmaster.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint("SetTextI18n")
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private Button signupBtn;
    private CheckBox deviceRememberCheckBox;
    private TextView forgetPassword;
    private String emailString;
    private String passwordString;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findAllViewById();

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
        email = findViewById(R.id.email_box);
        password = findViewById(R.id.password_box);
        loginBtn = findViewById(R.id.login_button);
        signupBtn = findViewById(R.id.create_account_button);
        forgetPassword = findViewById(R.id.forget_password);
        deviceRememberCheckBox = findViewById(R.id.remember_device_checkBox);
        loadingDialog = new LoadingDialog(LoginActivity.this);

    }

    private void loginButtonAction() {

        if (TextUtils.isEmpty(emailString) || emailString.contains(" ")) {
            email.setError("Enter Email");
        } else if (TextUtils.isEmpty(passwordString)) {
            password.setError("Enter Password");
        } else {
            runProgressDialog();
            login();
        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }

    }

    private void login() {


        Amplify.Auth.signIn(
                emailString,
                passwordString,
                result -> {
                    Log.i(TAG, result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    if (result.isSignInComplete()){
                        fetchCurrentUserAttributes();
                        if (deviceRememberCheckBox.isChecked()) {
                            rememberDevice();
                        }
                        savePasswordSharedPreferences();
                        runOnUiThread(this::navigateToMainActivity);
                    }else {
                        runOnUiThread(() -> {
                            dismissDialog();
                            Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            onResume();
                        });

                    }

                },
                error -> {
                    Log.e(TAG, error.toString());
                    runOnUiThread(() -> {
                        dismissDialog();
                        Toast.makeText(this, "username or Password Incorrect", Toast.LENGTH_SHORT).show();
                        onResume();
                    });
                }
        );

    }

    private void buttonsAction() {

        signupBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        loginBtn.setOnClickListener(view -> {
            getAllAsString();
            loginButtonAction();
        });

        forgetPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });
    }


    private void getAllAsString() {

        emailString = email.getText().toString().trim();
        passwordString = password.getText().toString().trim();
    }

    private void rememberDevice() {
        Amplify.Auth.rememberDevice(
                () -> Log.i(TAG, "Remember device succeeded"),
                error -> Log.e(TAG, "Remember device failed with error " + error)
        );
    }

    private void fetchCurrentUserAttributes() {

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i(TAG, "User attributes = " + attributes);
                    sendInfoToUserInfoClass(attributes);
                    UserInfo.saveOtherUserInfoToSharedPreferences(this);
                },
                error -> Log.e(TAG, "Failed to fetch user attributes.", error)
        );
    }

    private void navigateToMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);

    }

    private void runProgressDialog() {
        loadingDialog.startLoadingDialog();
    }

    private void dismissDialog() {

        loadingDialog.dismissLoadingDialog();
    }

    private void savePasswordSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(emailString, passwordString);
        preferenceEditor.apply();
    }


    private void sendInfoToUserInfoClass(List<AuthUserAttribute> userAttributesParam) {

        Log.i(TAG, "The user team from attributes para in login page is : " + userAttributesParam.get(0).getValue());
        UserInfo.firstName = userAttributesParam.get(3).getValue();
        UserInfo.lastName = userAttributesParam.get(4).getValue();
        UserInfo.email = userAttributesParam.get(5).getValue();
        UserInfo.userTeam = userAttributesParam.get(0).getValue();
        UserInfo.saveOtherUserInfoToSharedPreferences(this);
    }
}