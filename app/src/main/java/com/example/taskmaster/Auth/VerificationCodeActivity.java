package com.example.taskmaster.Auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;
import com.example.taskmaster.ui.LoadingDialog;

@RequiresApi(api = Build.VERSION_CODES.N)
public class VerificationCodeActivity extends AppCompatActivity {

    private static final String TAG = VerificationCodeActivity.class.getSimpleName();
    private EditText verificationCode;
    private Button checkBtn;
    private Button loginBtn;
    private String userEmailString;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        findViewsById();
        getUserEmailFromIntent();
        buttonsAction();
    }

    private void findViewsById(){
        verificationCode = findViewById(R.id.verification_code_box);
        checkBtn = findViewById(R.id.check_verification_code_button);
        loginBtn = findViewById(R.id.go_to_login_page_button);
        loadingDialog = new LoadingDialog(VerificationCodeActivity.this);
    }

    private void getUserEmailFromIntent(){

        userEmailString = getIntent().getStringExtra("emailFromSignUp");
    }

    private void buttonsAction(){

        checkBtn.setOnClickListener(view -> {
            loadingDialog.startLoadingDialog();
            checkVerificationCode();
        });
        loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        });
    }

    private void checkVerificationCode(){

        String verificationCodeString = verificationCode.getText().toString();
        Amplify.Auth.confirmSignUp(
                userEmailString,
                verificationCodeString,
                result -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    loadingDialog.dismissLoadingDialog();
                    finish();
                },
                error -> {
                    {
                        runOnUiThread(() -> {
                            loadingDialog.dismissLoadingDialog();
                            Toast.makeText(this, "Wrong verification code", Toast.LENGTH_SHORT).show();
                        });
                        Log.e(TAG, "Check error => "+error);
                    }
                }
        );
    }

}