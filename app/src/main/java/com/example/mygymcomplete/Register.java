package com.example.mygymcomplete;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName, email, password, phone;
    Button registerBtn;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    CheckBox isAdmin, isUser;

    @Override
    public void onBackPressed() {
        // Navigate back to the login activity
        super.onBackPressed();
        Intent intent = new Intent(Register.this, Login1.class);
        startActivity(intent);
        finish(); // Optional, if you want to finish the signup activity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.Username);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.MobNo);
        registerBtn = findViewById(R.id.btn_SignUp);
        isAdmin = findViewById(R.id.Owner);
        isUser = findViewById(R.id.User);

        isUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAdmin.setChecked(false);
                }
            }
        });

        isAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isUser.setChecked(false);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = checkfield();

                // Check whether checkbox is filled or not
                if (!(isAdmin.isChecked() || isUser.isChecked())) {
                    Toast.makeText(Register.this, "Select Account Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (flag) {
                    fauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fauth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fstore.collection("users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Username", fullName.getText().toString());
                            userInfo.put("Email", email.getText().toString());
                            userInfo.put("Phone Number", phone.getText().toString());
                            if (isAdmin.isChecked()) {
                                userInfo.put("isAdmin", "1");
                            }
                            if (isUser.isChecked()) {
                                userInfo.put("isUser", "1");
                            }

                            df.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Navigate to login activity after account creation
                                    Intent intent = new Intent(Register.this, Login1.class);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public boolean checkfield() {
        String username = fullName.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userPhone = phone.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Email is required.");
            return false;
        }

        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Password is required.");
            return false;
        }

        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters long.");
            return false;
        }

        if (TextUtils.isEmpty(userPhone)) {
            phone.setError("Mobile Number is required.");
            return false;
        }
        if (userPhone.length() != 10) {
            phone.setError("Phone number must be 10 digit.");
            return false;
        }
        if (TextUtils.isEmpty(username)) {
            fullName.setError("Username is required.");
            return false;
        }
        return true;
    }
}
