package com.example.taskmaster.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;

import java.net.URI;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UserDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;

    private String firstName;
    private String lastName;
    private String email;
    private String team;
    private int noOfTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        findAllView();

        getInfoFromUserInfoClass();

        setTheCardInfo();
    }

    private void findAllView(){

        imageView = findViewById(R.id.image_view_user_details);
        textView = findViewById(R.id.name_in_card_view);
        textView2 = findViewById(R.id.email_in_card_view);
        textView3 = findViewById(R.id.team_in_card_view);
        textView4 = findViewById(R.id.task_number_in_card_view);

    }

    private void getInfoFromUserInfoClass(){

        firstName = UserInfo.firstName;
        lastName = UserInfo.lastName;
        email = UserInfo.email;
        team = UserInfo.userTeam;
        noOfTasks = getIntent().getIntExtra("tasksListSize",5);
    }

    @SuppressLint("SetTextI18n")
    private void setTheCardInfo(){

        Bitmap bMap = BitmapFactory.decodeFile("/data/data/com.example.taskmaster/files/first_task_bxeearisnq.jpg");
        imageView.setImageBitmap(bMap);
        textView.setText("  Full name: "+firstName+" "+lastName);
        textView2.setText("  Email: "+email);
        textView3.setText("  Team: "+team);
        textView4.setText("  No of team Tasks: "+noOfTasks);
    }
}