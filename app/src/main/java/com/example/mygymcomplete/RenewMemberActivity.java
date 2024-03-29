package com.example.mygymcomplete;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RenewMemberActivity extends AppCompatActivity {

    private TextView startDateTextView, dueDateTextView, memberNameTextView, memberIdTextView;
    private EditText totalAmountEditText, dueAmountEditText, paidAmountEditText;
    private Button updateButton, deleteButton;
    private DatabaseReference databaseRef;
    private String memberId, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_member);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("members");

        // Initialize views
        startDateTextView = findViewById(R.id.startDateTextView);
        dueDateTextView        = findViewById(R.id.dueDateTextView);
        memberNameTextView = findViewById(R.id.memberNameTextView);
        memberIdTextView = findViewById(R.id.memberIdTextView);
        totalAmountEditText = findViewById(R.id.totalAmountEditText);
        dueAmountEditText = findViewById(R.id.dueAmountEditText);
        paidAmountEditText = findViewById(R.id.paidAmountEditText);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get member ID and name from intent

        memberId = getIntent().getStringExtra("memberId");
        memberNameTextView.setText("Member Name: " +fullName);

        // Set member name and ID in TextViews
        memberNameTextView.setText("Name"+fullName);
        memberIdTextView.setText("ID: " + memberId);

        // Set click listener for startDateTextView to show DatePicker dialog
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateTextView);
            }
        });

        // Set click listener for dueDateTextView to show DatePicker dialog
        dueDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(dueDateTextView);
            }
        });

        // Set click listener for update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMember();
            }
        });

        // Set click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMember();
            }
        });
    }

    // Method to show DatePicker dialog
    private void showDatePickerDialog(final TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RenewMemberActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateTextView.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Method to update member details
    private void updateMember() {
        String startDate = startDateTextView.getText().toString();
        String dueDate = dueDateTextView.getText().toString();
        String totalAmount = totalAmountEditText.getText().toString();
        String dueAmount = dueAmountEditText.getText().toString();
        String paidAmount = paidAmountEditText.getText().toString();

        // Update the member's data in Firebase
        databaseRef.child(memberId).child("startDate").setValue(startDate);
        databaseRef.child(memberId).child("dueDate").setValue(dueDate);
        databaseRef.child(memberId).child("totalAmount").setValue(totalAmount);
        databaseRef.child(memberId).child("dueAmount").setValue(dueAmount);
        databaseRef.child(memberId).child("paidAmount").setValue(paidAmount);

        // Notify the user about the successful update
        Toast.makeText(this, "Member information updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Method to delete member
    private void deleteMember() {
        // Delete the member's data from Firebase
        databaseRef.child(memberId).removeValue();

        // Notify the user about the successful deletion
        Toast.makeText(this, "Member deleted successfully", Toast.LENGTH_SHORT).show();

        // Finish the activity
        finish();
    }
}

