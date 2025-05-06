package com.example.myapplication.SETUP;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SetUp extends AppCompatActivity {
    public SetUp(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
