package com.example.mygymcomplete;



import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MembersDetailsActivity extends AppCompatActivity {

    private TextView memberNameTextView, memberIdTextView, memberPhoneTextView, memberEmailTextView,
            registrationCodeTextView, startDateTextView, dueDateTextView, totalAmountTextView,
            dueAmountTextView, paidAmountTextView, ageTextView, weightTextView, heightTextView,
            healthIssuesTextView, genderTextView, selectedPackageTextView;
    private ImageView memberProfileImageView;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_details);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("members");

        // Initialize views
        memberNameTextView = findViewById(R.id.fullNameTextView);
        memberIdTextView = findViewById(R.id.memberIdTextView);
        memberPhoneTextView = findViewById(R.id.mobileNoTextView);
        memberEmailTextView = findViewById(R.id.emailTextView);
        registrationCodeTextView = findViewById(R.id.registrationCodeTextView);
        startDateTextView = findViewById(R.id.startDateTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        dueAmountTextView = findViewById(R.id.dueAmountTextView);
        paidAmountTextView = findViewById(R.id.paidAmountTextView);
        ageTextView = findViewById(R.id.ageTextView);
        weightTextView = findViewById(R.id.weightTextView);
        heightTextView = findViewById(R.id.heightTextView);
        healthIssuesTextView = findViewById(R.id.healthIssuesTextView);
        genderTextView = findViewById(R.id.genderTextView);
        selectedPackageTextView = findViewById(R.id.currentPackageTextView);
        memberProfileImageView = findViewById(R.id.memberProfileImageView);

        // Get member ID from intent
        String memberId = getIntent().getStringExtra("memberId");

        // Fetch and display member details
        fetchMemberDetails(memberId);
    }

    private void fetchMemberDetails(String memberId) {
        databaseRef.child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GymMember member = dataSnapshot.getValue(GymMember.class);
                    displayMemberDetails(member);
                } else {
                    Toast.makeText(MembersDetailsActivity.this, "Member not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MembersDetailsActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMemberDetails(GymMember member) {
        memberNameTextView.setText("Name: " + member.getFullName());
        memberIdTextView.setText("Member ID: " + member.getMemberId());
        memberPhoneTextView.setText("Phone: " + member.getMobileNo());
        memberEmailTextView.setText("Email: " + member.getEmail());
        registrationCodeTextView.setText("Registration Code: " + member.getRegistrationCode());
        startDateTextView.setText("Start Date: " + member.getStartDate());
        dueDateTextView.setText("Due Date: " + member.getDueDate());
        totalAmountTextView.setText("Total Amount: " + member.getTotalAmount());
        dueAmountTextView.setText("Due Amount: " + member.getDueAmount());
        paidAmountTextView.setText("Paid Amount: " + member.getPaidAmount());
        ageTextView.setText("Age: " + member.getAge());
        weightTextView.setText("Weight: " + member.getWeight());
        heightTextView.setText("Height: " + member.getHeight());
        healthIssuesTextView.setText("Health Issues: " + member.getHealthIssues());
        genderTextView.setText("Gender: " + member.getGender());
        selectedPackageTextView.setText("Current Package: " + member.getSelectedPackage());

        // Load profile picture using Glide library
        Glide.with(this)
                .load(member.getPhotoUrl())
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(memberProfileImageView);
    }
}
