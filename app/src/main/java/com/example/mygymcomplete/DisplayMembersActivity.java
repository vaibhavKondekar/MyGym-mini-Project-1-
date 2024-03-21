package com.example.mygymcomplete;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymcomplete.AddMemberActivity;
import com.example.mygymcomplete.GymMember;
import com.example.mygymcomplete.MemberAdapter;
import com.example.mygymcomplete.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayMembersActivity extends AppCompatActivity implements MemberAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<GymMember> memberList;

    // Firebase
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_members);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("members");

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize member list and adapter
        memberList = new ArrayList<>();
        adapter = new MemberAdapter(memberList, this);

        // Set adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        // Set click listener for the "Add Member" button
        Button btnAddMember = findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayMembersActivity.this, AddMemberActivity.class));
            }
        });

        // Fetch gym member data from Firebase
        fetchMembersFromFirebase();
    }

    private void fetchMembersFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GymMember member = snapshot.getValue(GymMember.class);
                    memberList.add(member);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // Handle item click here (e.g., view member details)
        // You can implement this based on your requirements
    }

    @Override
    public void onRenewClick(int position) {
        // Handle renew button click here
        // You can implement this based on your requirements
    }
}
