package com.example.taskmaster.Auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
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

import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;
import com.example.taskmaster.ui.LoadingDialog;
import com.example.taskmaster.ui.SettingActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    private EditText newPassword;
    private EditText confirmNewPassword;
    private EditText verificationCodeOrCurrentPassword;
    private CheckBox signOutFromAllDevicesCheckbox;
    private Button resetPasswordBtn;
    private String newPasswordString;
    private boolean isFromSettingsActivity;
    private String confirmNewPasswordString;
    private String currentPassword;
    private String currentEmail;
    private String verificationCodeOrCurrentPasswordString;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        checkFromAnyActivity();

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


    private void checkFromAnyActivity() {

        isFromSettingsActivity = Objects.equals(getIntent().getStringExtra(SettingActivity.ACTIVITY), SettingActivity.class.getSimpleName());
    }

    @SuppressLint("SetTextI18n")
    private void findAllViewById() {
        TextView pageTitle = findViewById(R.id.reset_password_Text);
        newPassword = findViewById(R.id.reset_password_box);
        confirmNewPassword = findViewById(R.id.confirm_reset_password_box);
        verificationCodeOrCurrentPassword = findViewById(R.id.verification_code_box);
        signOutFromAllDevicesCheckbox = findViewById(R.id.sign_out_from_all_devices_checkbox);
        resetPasswordBtn = findViewById(R.id.password_reset_button);
        TextInputLayout textInputLayout = findViewById(R.id.styled_edit_text_verification_code);
        loadingDialog = new LoadingDialog(ResetPasswordActivity.this);
        currentEmail = UserInfo.getDefaults(UserInfo.EMAIL, null, this);
        currentPassword = UserInfo.getDefaults(currentEmail, null, this);
        Log.i(TAG, "resetPasswordButtonAction: Current email -> "+currentEmail);
        Log.i(TAG, "resetPasswordButtonAction: Current password -> "+currentPassword);

        if (isFromSettingsActivity) {
            textInputLayout.setHint("Current password");
            verificationCodeOrCurrentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pageTitle.setText("Change Password");
        } else {
            textInputLayout.setHint("Verification Code");
        }
    }

    private void buttonsAction() {

        resetPasswordBtn.setOnClickListener(view -> {
            getAllAsString();
            resetPasswordButtonAction();
        });
    }

    private void getAllAsString() {

        newPasswordString = newPassword.getText().toString();
        confirmNewPasswordString = confirmNewPassword.getText().toString();
        verificationCodeOrCurrentPasswordString = verificationCodeOrCurrentPassword.getText().toString().trim();
    }

    private void resetPasswordButtonAction() {

        if (TextUtils.isEmpty(newPassword.getText())) {

            newPassword.setError("Enter new Password");

        } else if (TextUtils.isEmpty(confirmNewPassword.getText())) {

            confirmNewPassword.setError("Confirm new Password!");

        } else if (!newPasswordString.equals(confirmNewPasswordString)) {

            newPassword.setError("Password Mismatch");
            confirmNewPassword.setError("Password Mismatch");

        } else if (newPasswordString.length() < 8) {

            newPassword.setError("Password too short");
            confirmNewPassword.setError("Password too short");

        } else if (TextUtils.isEmpty(verificationCodeOrCurrentPassword.getText())) {

            if (isFromSettingsActivity) {

                verificationCodeOrCurrentPassword.setError("Enter current password");

            } else {

                verificationCodeOrCurrentPassword.setError("Enter verification code");

            }

        } else if (!verificationCodeOrCurrentPasswordString.equals(currentPassword) && isFromSettingsActivity) {
            verificationCodeOrCurrentPassword.setError("Wrong password");

        } else if (newPasswordString.equals(verificationCodeOrCurrentPasswordString) && isFromSettingsActivity) {
            newPassword.setError("Password not Edited");

        } else if (!isFromSettingsActivity) {
            showLoadingDialog();
            Amplify.Auth.confirmResetPassword(
                    newPasswordString,
                    verificationCodeOrCurrentPasswordString,
                    () -> {
                        Log.i(TAG, "New password confirmed");
                        updatePasswordInSharedPreferences();
                        if (signOutFromAllDevicesCheckbox.isChecked()) {
                            signOutFromAllDevices();
                        } else {
                            signOut();
                        }
                    },
                    error -> {
                        Log.e(TAG, error.toString());
                        runOnUiThread(() -> {
                            loadingDialog.dismissLoadingDialog();
                            Toast.makeText(this, "Change can't complete something went wrong", Toast.LENGTH_SHORT).show();
                            onResume();
                        });
                    }
            );
        } else {
            showLoadingDialog();
            /*
            The verification code here is current password in case the user are
            already signed in and he want to change his\her password
            */
            Amplify.Auth.updatePassword(verificationCodeOrCurrentPasswordString,
                    newPasswordString,
                    () -> {
                        Log.i(TAG, "Password Updated");
                        updatePasswordInSharedPreferences();
                        if (signOutFromAllDevicesCheckbox.isChecked()) {
                            signOutFromAllDevices();
                        }
                            signOut();
                    },
                    failure -> {
                        Log.i(TAG, "Password not Updated " + failure);
                        runOnUiThread(() -> {
                            loadingDialog.dismissLoadingDialog();
                            Toast.makeText(this, "Change can't complete something went wrong", Toast.LENGTH_SHORT).show();
                            onResume();
                        });
                    });
        }
        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }

    private void showLoadingDialog() {
        loadingDialog.startLoadingDialog();
    }

    private void updatePasswordInSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(currentEmail, newPasswordString);
        preferenceEditor.apply();
    }

    private void signOutFromAllDevices() {

        Amplify.Auth.signOut(
                AuthSignOutOptions.builder().globalSignOut(true).build(),
                () -> Log.i(TAG, "Signed out globally"),
                error -> Log.e(TAG, error.toString())
        );

    }

    private void signOut() {

        Amplify.Auth.signOut(
                () -> {
                    Log.i(TAG, "Signed out successfully");
                    navigateToLoginPage();
                },
                error -> {
                    Log.e(TAG, error.toString());
                    runOnUiThread(() -> {
                        loadingDialog.dismissLoadingDialog();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        onResume();
                    });
                }
        );
    }

    private void navigateToLoginPage() {
        loadingDialog.dismissLoadingDialog();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
