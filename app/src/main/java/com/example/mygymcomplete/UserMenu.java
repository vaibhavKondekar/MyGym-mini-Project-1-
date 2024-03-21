package com.example.mygymcomplete;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class UserMenu extends AppCompatActivity {
    ImageView ScanQR, GymBuddy;
    Animation topAnim, bottomAnim;
    FirebaseAuth auth;
    Button button;
    FirebaseUser user;
    // Define the stored QR code
    String storedQR = "abcd"; // Replace with your stored QR code content

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                // Compare scanned QR with stored QR
                if(contents.equals(storedQR)) {
                    // Show success dialog
                    showSuccessDialog();
                } else {
                    // Show failure dialog
                    showFailureDialog();
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_menu);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(UserMenu.this, Login1.class);
            startActivity(intent);
            finish();
        }

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        GymBuddy = findViewById(R.id.GymBuddy);
        GymBuddy.setAnimation(bottomAnim);

        ScanQR=findViewById(R.id.ScanQR);
        ScanQR.setAnimation(topAnim);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserMenu.this, Login1.class);
                startActivity(intent);
                finish();
            }
        });

        GymBuddy=findViewById(R.id.GymBuddy);
        GymBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenu.this, GymBuddy.class);
                startActivity(intent);
            }
        });

        ScanQR = findViewById(R.id.ScanQR);
        ScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
    }

    // Method to start QR code scanning
    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR Code");
        integrator.initiateScan();
    }

    // Method to show dialog for successful attendance
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserMenu.this);
        builder.setTitle("Attendance Marked");
        builder.setMessage("Attendance marked successfully!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    // Method to show dialog for failed attendance
    private void showFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserMenu.this);
        builder.setTitle("Attendance Not Marked");
        builder.setMessage("Failed to mark attendance. QR code does not match.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
