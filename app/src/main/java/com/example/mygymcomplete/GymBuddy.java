package com.example.mygymcomplete;


import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class GymBuddy extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        // Navigate back to the login activity
        super.onBackPressed();
        Intent intent = new Intent(GymBuddy.this, UserMenu.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_buddy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}