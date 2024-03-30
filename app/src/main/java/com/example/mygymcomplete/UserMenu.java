package com.example.mygymcomplete;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserMenu extends AppCompatActivity {
    private static final String TAG = "UserMenu";
    ImageView ScanQR, GymBuddy;
    FirebaseAuth auth;
    Button button;
    FirebaseUser user;
    // Define the stored QR code
    String storedQR = "abcd"; // Replace with your stored QR code content

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_menu);
        Log.d(TAG, "onCreate: ");

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(UserMenu.this, Login1.class);
            startActivity(intent);
            finish();
        }

        GymBuddy = findViewById(R.id.GymBuddy);
        ScanQR = findViewById(R.id.ScanQR);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserMenu.this, Login1.class);
                startActivity(intent);
                finish();
            }
        });

        GymBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenu.this, GymBuddy.class);
                startActivity(intent);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            Log.d(TAG, "onActivityResult: scanned contents=" + contents);
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

    // Method to show dialog for successful attendance
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserMenu.this);
        builder.setTitle("Attendance Marked");
        builder.setMessage("Mark Your Attendance");

        // Set the positive button (Check In)
        builder.setPositiveButton("Check In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(UserMenu.this, "Attendance Marked for Check In.", Toast.LENGTH_SHORT).show();
                // Add your check-in logic here
                markAttendance("Check In");
            }
        });

        // Set the negative button (Check Out)
        builder.setNegativeButton("Check Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(UserMenu.this, "Attendance Marked for Check Out.", Toast.LENGTH_SHORT).show();
                // Add your check-out logic here
                markAttendance("Check Out");
            }
        });

        builder.show();
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

    // Function to mark attendance
    // Function to mark attendance
    private void markAttendance(String attendanceType) {
        // Get current user details
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();

            // Get current date and time
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Create a reference to your Firebase Realtime Database
            DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance").child(userID);

            // Create a new entry in the database for the attendance
            String attendanceKey = attendanceRef.push().getKey(); // Generate a unique key for the attendance entry
            HashMap<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("timestamp", timestamp);
            attendanceData.put("type", attendanceType);

            // Check if it's a check-in or check-out
            if (attendanceType.equals("Check In")) {
                // Retrieve username from Firestore
                FirebaseFirestore.getInstance().collection("users").document(userID)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userName = documentSnapshot.getString("Username");
                                if (userName != null && !userName.isEmpty()) {
                                    attendanceData.put("name", userName); // Include user's name in attendance data
                                    // Push the data to Firebase under a unique key
                                    attendanceRef.child(attendanceKey).setValue(attendanceData)
                                            .addOnSuccessListener(aVoid -> {
                                                // Attendance marked successfully
                                                Log.d(TAG, "markAttendance: Check-in marked successfully");
                                                Toast.makeText(UserMenu.this, "Check-in marked successfully.", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Error occurred while marking attendance
                                                Log.e(TAG, "markAttendance: Error marking check-in", e);
                                                Toast.makeText(UserMenu.this, "Failed to mark check-in. Please try again.", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.e(TAG, "markAttendance: User name is null or empty");
                                }
                            } else {
                                Log.e(TAG, "markAttendance: Document does not exist");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "markAttendance: Error fetching user data", e);
                            Toast.makeText(UserMenu.this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                        });
            } else if (attendanceType.equals("Check Out")) {
                // Push the data to Firebase under a unique key
                attendanceRef.child(attendanceKey).setValue(attendanceData)
                        .addOnSuccessListener(aVoid -> {
                            // Attendance marked successfully
                            Log.d(TAG, "markAttendance: Check-out marked successfully");
                            Toast.makeText(UserMenu.this, "Check-out marked successfully.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Error occurred while marking attendance
                            Log.e(TAG, "markAttendance: Error marking check-out", e);
                            Toast.makeText(UserMenu.this, "Failed to mark check-out. Please try again.", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Log.e(TAG, "markAttendance: Current user is null");
        }
    }


}
