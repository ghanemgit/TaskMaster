package com.example.taskmaster.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.taskmaster.R;

/*
https://www.youtube.com/watch?v=tccoRIrMyhU&ab_channel=Stevdza-San
how to make a progress alert while doing things need to some time like login process
*/
/*
* https://stackoverflow.com/a/29757559/19143106
* How to make my progress alert spinning(Rotating)
* because the built in progress bar doesn't spinning or rotating
*/
public class LoadingDialog {

    private final Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_loading_dialog,null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissLoadingDialog(){

        alertDialog.dismiss();
    }
}
