package com.example.mygymcomplete;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class splasherActivity extends AppCompatActivity {
    FirebaseAuth fauth;
    FirebaseFirestore fstore;

    @Override
    public void onStart() {
        super.onStart();
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = fauth.getCurrentUser();
        if (currentUser != null) {
            checkUserAccessLevel(currentUser.getUid());
        } else {
            startLoginActivity();
        }
    }

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splasher);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(splasherActivity.this, Login1.class);
        startActivity(intent);
        finish();
    }

    private void checkUserAccessLevel(String Uid) {
        DocumentReference df = fstore.collection("users").document(Uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: " + documentSnapshot.getData());
                    if (documentSnapshot.getString("isAdmin") != null) {
                        // User is admin
                        startActivity(new Intent(splasherActivity.this, AdminMenu.class));
                        finish();
                    } else if (documentSnapshot.getString("isUser") != null) {
                        // User is regular user
                        startActivity(new Intent(splasherActivity.this, UserMenu.class));
                        finish();
                    } else {
                        // User data does not match expected roles
                        startLoginActivity();
                    }
                } else {
                    // Document does not exist
                    startLoginActivity();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error fetching document
                Log.d("TAG", "Error getting document: " + e);
                startLoginActivity();
            }
        });
    }
}
