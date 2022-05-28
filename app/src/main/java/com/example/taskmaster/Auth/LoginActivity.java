package com.example.taskmaster.Auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;
import com.example.taskmaster.ui.LoadingDialog;
import com.example.taskmaster.ui.MainActivity;
import com.example.taskmaster.ui.SplashActivity;

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
                    if (deviceRememberCheckBox.isChecked()) {
                        rememberDevice();
                    }
                    fetchCurrentUserAttributes();
                    navigateToMainActivity();
                },
                error -> {
                    Log.e(TAG, error.toString());
                    runOnUiThread(() -> {
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(this, "username or Password Incorrect", Toast.LENGTH_SHORT).show();
                        onResume();
                    });
                }
        );

    }

    private void buttonsAction() {

        signupBtn.setOnClickListener(view -> {
            loadingDialog.startLoadingDialog();
            startActivity(new Intent(this, SignUpActivity.class));
            loadingDialog.dismissLoadingDialog();
        });

        loginBtn.setOnClickListener(view -> {
            getAllAsString();
            loginButtonAction();
        });

        forgetPassword.setOnClickListener(view -> {
            loadingDialog.startLoadingDialog();
            startActivity(new Intent(this, ForgetPasswordActivity.class));
            loadingDialog.dismissLoadingDialog();

        });
    }



    private void getAllAsString() {

        emailString = email.getText().toString().trim();
        passwordString = password.getText().toString().trim();
    }

    private void rememberDevice(){
        Amplify.Auth.rememberDevice(
                () -> Log.i(TAG, "Remember device succeeded"),
                error -> Log.e(TAG, "Remember device failed with error " + error)
        );
    }

    private void fetchCurrentUserAttributes(){

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i(TAG, "User attributes = " + attributes);
                    SplashActivity.userAttributes = attributes;
                },
                error -> Log.e(TAG, "Failed to fetch user attributes.", error)
        );
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        dismissDialog();
        finish();
    }

    private void runProgressDialog(){


        loadingDialog.startLoadingDialog();

    }

    private void dismissDialog(){

        loadingDialog.dismissLoadingDialog();
    }


}