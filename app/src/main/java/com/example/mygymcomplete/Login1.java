package com.example.mygymcomplete;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login1 extends AppCompatActivity {
    EditText email,password;
    Button loginBtn;
    TextView SignUp;
    boolean valid = true;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = fauth.getCurrentUser();
        if (currentUser != null) {
            checkUserAccessLevel(currentUser.getUid());
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login1);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.btn_login);
        SignUp=findViewById(R.id.SignUpNow);


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login1.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(email);
                checkField(password);

                if (valid) {
                    fauth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login1.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            checkUserAccessLevel(authResult.getUser().getUid()); // Check access level after successful login
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login1.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });






    }

    private void checkUserAccessLevel(String Uid)
    {
        String uid = null;
        DocumentReference df=fstore.collection("users").document(Uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onsuccess"+documentSnapshot.getData());
                if(documentSnapshot.getString("isAdmin") != null)
                {
                    //user is admin
                    startActivity(new Intent(Login1.this,AdminMenu.class));
                    finish();
                }
                if(documentSnapshot.getString("isUser") != null)
                {
                    //user
                    startActivity(new Intent(Login1.this, UserMenu.class));
                    finish();
                }


            }
        });
    }




    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

}

