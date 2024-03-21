package com.example.mygymcomplete;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMemberActivity extends AppCompatActivity {

    private EditText fullNameEditText, mobileNoEditText, emailEditText, registrationCodeEditText,
            startDateEditText, dueDateEditText, totalAmountEditText, dueAmountEditText, paidAmountEditText, ageEditText;

    private RadioButton maleRadioButton, femaleRadioButton;
    private Spinner packageSpinner;
    private Button addMemberButton, uploadPhotoButton;
    private Calendar calendar;
    private int year, month, day;

    // Firebase
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    // Request code for image selection
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("members");
        storageRef = FirebaseStorage.getInstance().getReference("member_photos");

        // Initialize views
        fullNameEditText = findViewById(R.id.fullNameEditText);
        mobileNoEditText = findViewById(R.id.mobileNoEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registrationCodeEditText = findViewById(R.id.registrationCodeEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        totalAmountEditText = findViewById(R.id.totalAmountEditText);
        dueAmountEditText = findViewById(R.id.dueAmountEditText);
        paidAmountEditText = findViewById(R.id.paidAmountEditText);
        ageEditText = findViewById(R.id.ageEditText);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        packageSpinner = findViewById(R.id.packageSpinner);
        addMemberButton = findViewById(R.id.addMemberButton);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);

        // Initialize calendar instance
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set click listeners
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateEditText);
            }
        });

        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(dueDateEditText);
            }
        });

        // Populate spinner with package options
        String[] packages = {"Monthly (Rs. 800)", "Three Months (Rs. 2000)", "Annual (Rs. 7000)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, packages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packageSpinner.setAdapter(adapter);

        // Set click listener for addMemberButton
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberToDatabase();
            }
        });

        // Set click listener for uploadPhotoButton
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
        }
    }

    private void addMemberToDatabase() {
        final String fullName = fullNameEditText.getText().toString();
        final String mobileNo = mobileNoEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String registrationCode = registrationCodeEditText.getText().toString();
        final String startDate = startDateEditText.getText().toString();
        final String dueDate = dueDateEditText.getText().toString();
        final String totalAmount = totalAmountEditText.getText().toString();
        final String dueAmount = dueAmountEditText.getText().toString();
        final String paidAmount = paidAmountEditText.getText().toString();
        final String age = ageEditText.getText().toString();
        final String gender = maleRadioButton.isChecked() ? "Male" : "Female";
        final String selectedPackage = packageSpinner.getSelectedItem().toString();

        // Check if any field is empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(mobileNo) || TextUtils.isEmpty(startDate)
                || TextUtils.isEmpty(dueDate) || TextUtils.isEmpty(totalAmount) || TextUtils.isEmpty(dueAmount)
                || TextUtils.isEmpty(paidAmount) || TextUtils.isEmpty(age) || TextUtils.isEmpty(registrationCode)) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if mobile number is valid
        if (mobileNo.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if registration code is unique
        databaseRef.orderByChild("registrationCode").equalTo(registrationCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(AddMemberActivity.this, "Registration code already taken, please choose another", Toast.LENGTH_SHORT).show();
                } else {
                    // Generate unique ID for the member
                    final String memberId = databaseRef.push().getKey();

                    // Default image URL
                    final String defaultImageUrl = "https://example.com/default_image.jpg";

                    // Upload photo to Firebase Storage if an image is selected
                    if (selectedImageUri != null) {
                        final StorageReference photoRef = storageRef.child(memberId + ".jpg");
                        photoRef.putFile(selectedImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String photoUrl = uri.toString();
                                                // Create GymMember object with the information
                                                GymMember gymMember = new GymMember(memberId, fullName, mobileNo, email, registrationCode,
                                                        startDate, dueDate, totalAmount, dueAmount, paidAmount, age, gender, selectedPackage, photoUrl);

                                                // Add GymMember object to Firebase database with the unique ID
                                                databaseRef.child(memberId).setValue(gymMember)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(AddMemberActivity.this, "New member successfully added", Toast.LENGTH_SHORT).show();
                                                                finish(); // Close activity after adding member
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AddMemberActivity.this, "Failed to add member", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddMemberActivity.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Create GymMember object with the information and default image URL
                        GymMember gymMember = new GymMember(memberId, fullName, mobileNo, email, registrationCode,
                                startDate, dueDate, totalAmount, dueAmount, paidAmount, age, gender, selectedPackage, defaultImageUrl);

                        // Add GymMember object to Firebase database with the unique ID
                        databaseRef.child(memberId).setValue(gymMember)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddMemberActivity.this, "New member successfully added", Toast.LENGTH_SHORT).show();
                                        finish(); // Close activity after adding member
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddMemberActivity.this, "Failed to add member", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddMemberActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(final EditText dateEditText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        dateEditText.setText(sdf.format(calendar.getTime()));
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }
}
