package com.example.mygymcomplete;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMenu extends AppCompatActivity {

    private Button btnAddMember, btnViewMembers, btnAttendance,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // Initialize buttons
        btnAddMember = findViewById(R.id.btnAddMember);
        btnViewMembers = findViewById(R.id.btnViewMembers);
        btnAttendance = findViewById(R.id.btnAttendance);
        logout=findViewById(R.id.logout);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminMenu.this, Login1.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listeners
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, AddMemberActivity.class));
            }
        });

        btnViewMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, DisplayMembersActivity.class));
            }
        });

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, AttendanceActivity.class));
            }
        });
    }
}
