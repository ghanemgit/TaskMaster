package com.example.taskmaster.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
    private EditText resetEmail;
    private Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        findAllViewById();
        buttonsAction();
    }

    private void findAllViewById(){

        resetEmail = findViewById(R.id.forget_password_email_box);
        resetBtn = findViewById(R.id.send_verification_code_button);
    }
    private void buttonsAction(){
        resetBtn.setOnClickListener(view -> {
            resetButtonAction();
        });
    }

    private void resetButtonAction(){
        if (TextUtils.isEmpty(resetEmail.getText())) {
            resetEmail.setError("Enter Email");
        }else {
            sendVerificationCode();
        }
        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }

    private void sendVerificationCode(){

        Amplify.Auth.resetPassword(
                resetEmail.getText().toString(),
                result -> {

                    navigateToResetPasswordActivity();
                    Log.i(TAG, result.toString());

                },
                error -> Log.e(TAG, error.toString())
        );

    }

    private void navigateToResetPasswordActivity(){
        Intent intent = new Intent(this,ResetPasswordActivity.class);
        intent.putExtra(UserInfo.EMAIL,resetEmail.getText().toString());
        startActivity(intent);

        finish();
    }

}