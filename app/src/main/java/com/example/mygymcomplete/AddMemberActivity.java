package com.example.mygymcomplete;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMemberActivity extends AppCompatActivity {

    private EditText fullNameEditText, mobileNoEditText, emailEditText, registrationCodeEditText,
            startDateEditText, dueDateEditText, totalAmountEditText, dueAmountEditText, paidAmountEditText, ageEditText,
            weightEditText, heightEditText, healthIssuesEditText;

    private RadioButton maleRadioButton, femaleRadioButton;
    private Spinner packageSpinner;
    private Button addMemberButton, takePhotoButton;
    private ImageView selectedImageView;
    private Calendar calendar;
    private int year, month, day;
    private Uri selectedImageUri;

    // Firebase
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

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
        weightEditText = findViewById(R.id.weightEditText);
        heightEditText = findViewById(R.id.heightEditText);
        healthIssuesEditText = findViewById(R.id.healthIssuesEditText);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        packageSpinner = findViewById(R.id.packageSpinner);
        addMemberButton = findViewById(R.id.addMemberButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        selectedImageView = findViewById(R.id.selectedImageView);

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

        // Set click listener for takePhotoButton
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddMemberActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddMemberActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImageView.setImageBitmap(imageBitmap);
            // Convert Bitmap to Uri
            selectedImageUri = getImageUri(this, imageBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void addMemberToDatabase() {
        // Retrieve member details from EditText fields
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
        final String weight = weightEditText.getText().toString();
        final String height = heightEditText.getText().toString();
        final String healthIssues = healthIssuesEditText.getText().toString();
        final String gender = maleRadioButton.isChecked() ? "Male" : "Female";
        final String selectedPackage = packageSpinner.getSelectedItem().toString();

        // Check if any field is empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(mobileNo) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(dueDate) ||
                TextUtils.isEmpty(totalAmount) || TextUtils.isEmpty(dueAmount) || TextUtils.isEmpty(paidAmount) ||
                TextUtils.isEmpty(age) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(height) || TextUtils.isEmpty(healthIssues) || TextUtils.isEmpty(registrationCode)) {
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
                                                        startDate, dueDate, totalAmount, dueAmount, paidAmount, age, weight, height, healthIssues, gender, selectedPackage, photoUrl);

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
                                startDate, dueDate, totalAmount, dueAmount, paidAmount, age, weight, height, healthIssues, gender, selectedPackage, defaultImageUrl);

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
