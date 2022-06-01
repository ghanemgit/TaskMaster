package com.example.taskmaster.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.Auth.EditUserDetailsActivity;
import com.example.taskmaster.Auth.LoginActivity;
import com.example.taskmaster.Auth.ResetPasswordActivity;
import com.example.taskmaster.Auth.SignUpActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;

@SuppressLint("ResourceAsColor")
@RequiresApi(api = Build.VERSION_CODES.N)
public class SettingActivity extends AppCompatActivity {

    public static final String NO_OF_TASK_TO_SHOW = "No of Task to show";
    private static final String TAG = SettingActivity.class.getSimpleName();
    public static final String ACTIVITY = "Activity";
    private ListView listView;
    private TextView textView;
    private final String[] itemList = {"Edit Profile","Change password","Delete account",""};
    private final String[] subItemsList = {"change name, and email"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewByIdMethod();

        setUserWelcoming();

        setListView();

        setOnFullNameListener();
    }

    private void findViewByIdMethod() {

        listView = findViewById(R.id.settings_list_views);
        textView = findViewById(R.id.user_welcoming_settings);
    }

    @SuppressLint("SetTextI18n")
    private void setUserWelcoming(){
        textView.setText(UserInfo.getDefaults(UserInfo.FIRST_NAME,"Guest",this)
                + " " + UserInfo.getDefaults(UserInfo.LAST_NAME,"",this));
    }

    private void setListView(){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, itemList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);

                TextView name = view.findViewById(android.R.id.text1);
                TextView subItem = view.findViewById(android.R.id.text2);


                /*
                 * How to set the text style from java side
                 * https://www.codegrepper.com/code-examples/whatever/make+text+bold+android+studio
                 */
                name.setTypeface(null, Typeface.BOLD);


                name.setText(itemList[position]);
                if (position==0) {
                    subItem.setText(subItemsList[position]);
                }

                return view;
            }
        };

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            switch (i){
                case 0:
                    navigateToEditPage();
                    break;
                case 1:
                    navigateToResetPasswordPage();
                    break;
                case 2:
                    deleteAccountAlertDialog();
                    break;
                default:
            }
        });
    }

    private void navigateToEditPage(){

        Intent intent = new Intent(this, EditUserDetailsActivity.class);
        startActivity(intent);

    }

    private void navigateToResetPasswordPage(){

        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra(ACTIVITY,SettingActivity.class.getSimpleName());
        intent.putExtra(UserInfo.EMAIL,UserInfo.getDefaults(UserInfo.EMAIL,null,this));
        intent.putExtra(UserInfo.getDefaults(UserInfo.EMAIL,null,this),UserInfo.getDefaults(UserInfo.PASSWORD,null,this));
        startActivity(intent);
    }

    private void deleteAccountAlertDialog() {
        /*
        https://stackoverflow.com/questions/33437398/how-to-change-textcolor-in-alertdialog
        how to change the text color in alert dialog
        */
        String random = randomString()+"";
        AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setMessage("Are you sure to delete your account?\n\nEnter "+ random + " To confirm");
        alert.setTitle(Html.fromHtml("<font color='#FF0000'>Warning!</font>"));

        alert.setView(edittext);

        alert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>ok</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edittext.getText().toString().equals(random+"")){
                    deleteAccount();
                    navigateToLoginPage();
                    finish();
                }else {
                    deleteAccountAlertDialog();
                }
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onResume();
            }
        });

        alert.show();
    }

    private int randomString(){
        return (int)(Math.random() * (5000 - 2000) + 1) + 2000;
    }

    private void deleteAccount(){

        Amplify.Auth.deleteUser(
                () -> Log.i(TAG, "Delete user succeeded"),
                error -> Log.e(TAG, "Delete user failed with error " + error.toString())
        );
    }

    private void navigateToLoginPage(){
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void setOnFullNameListener(){

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(this,UserDetailsActivity.class);
            intent.putExtra("tasksListSize",getIntent().getIntExtra("tasksListSize",5));
            startActivity(intent);
        });
    }
}