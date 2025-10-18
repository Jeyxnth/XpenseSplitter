package com.example.xpensesplitter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensesplitter.adapters.GroupAdapter;
import com.example.xpensesplitter.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerGroups;
    private List<Group> groupList = new ArrayList<>();
    private GroupAdapter adapter;
    private Button buttonCreateGroup, buttonJoinGroup, buttonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ Match the IDs from layout
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);
        buttonJoinGroup = findViewById(R.id.buttonJoinGroup);
        buttonProfile = findViewById(R.id.buttonProfile);
        recyclerGroups = findViewById(R.id.recyclerGroups);

        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter(groupList, group -> {
            Intent i = new Intent(MainActivity.this, GroupDetailsActivity.class);
            i.putExtra("groupId", group.getId());
            i.putExtra("groupName", group.getName());
            startActivity(i);
        });
        recyclerGroups.setAdapter(adapter);

        buttonCreateGroup.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CreateGroupActivity.class))
        );

        buttonJoinGroup.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, JoinGroupActivity.class))
        );

        buttonProfile.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class))
        );

        loadGroups();
    }

    private void loadGroups() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String email = user.getEmail();
        String name = user.getDisplayName();

        List<String> identifiers = new ArrayList<>();
        if (email != null) identifiers.add(email);
        if (name != null && !name.trim().isEmpty()) identifiers.add(name);

        if (identifiers.isEmpty()) {
            Toast.makeText(this, "User info missing. Please set your profile name.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("groups")
                .whereArrayContainsAny("members", identifiers)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Error loading groups: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    groupList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Group group = doc.toObject(Group.class);
                            group.setId(doc.getId());
                            groupList.add(group);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
