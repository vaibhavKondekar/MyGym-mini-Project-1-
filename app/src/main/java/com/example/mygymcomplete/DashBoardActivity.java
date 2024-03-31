package com.example.mygymcomplete;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymcomplete.GymMember;
import com.example.mygymcomplete.MemberAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashBoardActivity extends AppCompatActivity {

    private TextView textPresentDate, textTotalMembers, textExpiryToday, textExpiryNext3Days, textExpired;
    private RadioGroup radioGroup;
    private RadioButton radioExpirySoon, radioExpiringToday, radioExpired;
    private Button btnShowList;
    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;
    private List<GymMember> allMembersList;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("members");

        // Initialize views
        textPresentDate = findViewById(R.id.textPresentDate);
        textTotalMembers = findViewById(R.id.textTotalMembers);
        textExpiryToday = findViewById(R.id.textExpiryToday);
        textExpiryNext3Days = findViewById(R.id.textExpiryNext3Days);
        textExpired = findViewById(R.id.textExpiried);
        radioGroup = findViewById(R.id.radioGroup);
        radioExpirySoon = findViewById(R.id.radioExpirySoon);
        radioExpiringToday = findViewById(R.id.radioExpiringToday);
        radioExpired = findViewById(R.id.radioExpired);
        btnShowList = findViewById(R.id.btnShowList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allMembersList = new ArrayList<>();
        memberAdapter = new MemberAdapter(allMembersList, new MemberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }

            @Override
            public void onRenewClick(int position) {
                // Handle renew click if needed
            }
        });
        recyclerView.setAdapter(memberAdapter);

        // Set current date
        setCurrentDate();

        // Set listeners
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedList();
            }
        });

        // Load data from Firebase
        loadDataFromFirebase();
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String currentDate = sdf.format(new Date());
        textPresentDate.setText("Present Date: " + currentDate);
    }

    private void loadDataFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMembersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GymMember member = snapshot.getValue(GymMember.class);
                    allMembersList.add(member);
                }
                updateMemberCounts();
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashBoardActivity.this, "Failed to load data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMemberCounts() {
        int totalMembers = allMembersList.size();
        textTotalMembers.setText("Total Members: " + totalMembers);

        Calendar calendar = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(calendar.getTime());
        int expiryTodayCount = 0;
        int expiryNext3DaysCount = 0;
        int expiredCount = 0;

        for (GymMember member : allMembersList) {
            String dueDate = member.getDueDate();
            if (dueDate.equals(currentDate)) {
                expiryTodayCount++;
            } else {
                try {
                    Date expiryDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dueDate);
                    long diff = expiryDate.getTime() - calendar.getTimeInMillis();
                    long daysDiff = diff / (24 * 60 * 60 * 1000);
                    if (daysDiff <= 3 && daysDiff >= 0) {
                        expiryNext3DaysCount++;
                    } else if (daysDiff < 0) {
                        expiredCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        textExpiryToday.setText("Expiry Today: " + expiryTodayCount);
        textExpiryNext3Days.setText("Expiry in Next 3 Days: " + expiryNext3DaysCount);
        textExpired.setText("Expired: " + expiredCount);
    }

    private void filterListByExpirySoon() {
        List<GymMember> filteredList = new ArrayList<>();
        for (GymMember member : allMembersList) {
            if (isExpiringInNextThreeDays(member)) {
                filteredList.add(member);
            }
        }
        memberAdapter.filterList(filteredList);
    }

    private void filterListByExpiringToday() {
        List<GymMember> filteredList = new ArrayList<>();
        for (GymMember member : allMembersList) {
            if (isExpiringToday(member)) {
                filteredList.add(member);
            }
        }
        memberAdapter.filterList(filteredList);
    }

    private void filterListByExpired() {
        List<GymMember> filteredList = new ArrayList<>();
        for (GymMember member : allMembersList) {
            if (isExpired(member)) {
                filteredList.add(member);
            }
        }
        memberAdapter.filterList(filteredList);
    }

    private void showSelectedList() {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == R.id.radioExpirySoon) {
            filterListByExpirySoon();
        } else if (selectedRadioButtonId == R.id.radioExpiringToday) {
            filterListByExpiringToday();
        } else if (selectedRadioButtonId == R.id.radioExpired) {
            filterListByExpired();
        } else {
            Toast.makeText(this, "Please select a list", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isExpiringToday(GymMember member) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String currentDate = dateFormat.format(calendar.getTime());
        return member.getDueDate().equals(currentDate);
    }

    private boolean isExpiringInNextThreeDays(GymMember member) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String currentDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        String threeDaysAfterDate = dateFormat.format(calendar.getTime());
        return member.getDueDate().compareTo(currentDate) > 0 && member.getDueDate().compareTo(threeDaysAfterDate) <= 0;
    }

    private boolean isExpired(GymMember member) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String currentDate = dateFormat.format(calendar.getTime());
        return member.getDueDate().compareTo(currentDate) < 0;
    }
}

