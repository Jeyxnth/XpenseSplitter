package com.example.xpensesplitter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText inputGroupName;
    private Button buttonCreateGroup;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        inputGroupName = findViewById(R.id.inputGroupName);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonCreateGroup.setOnClickListener(v -> createGroup());
    }

    private void createGroup() {
        String groupName = inputGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "Enter group name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        String userName = currentUser.getDisplayName(); // 👈 Fetch the user's display name

        // If display name not set, fallback
        if (userName == null || userName.isEmpty()) {
            userName = "Unknown User";
        }

        // Add the creator as the first member
        List<String> members = new ArrayList<>();
        members.add(userEmail);

        // Create Firestore group document
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("name", groupName);
        groupData.put("createdBy", userEmail);
        groupData.put("createdByName", userName); // 👈 Store the creator's name
        groupData.put("members", members);
        groupData.put("createdAt", System.currentTimeMillis());

        // Save to Firestore
        db.collection("groups")
                .add(groupData)
                .addOnSuccessListener(documentReference -> showGroupCodeDialog(documentReference))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error creating group: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showGroupCodeDialog(DocumentReference documentReference) {
        String groupCode = documentReference.getId();

        new AlertDialog.Builder(this)
                .setTitle("🎉 Group Created Successfully!")
                .setMessage("Group Code: " + groupCode + "\n\nShare this code with your friends so they can join the group.")
                .setPositiveButton("Copy Code", (dialog, which) -> {
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Group Code", groupCode);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Group code copied!", Toast.LENGTH_SHORT).show();
                    goToMain();
                })
                .setNegativeButton("Done", (dialog, which) -> goToMain())
                .setCancelable(false)
                .show();
    }

    private void goToMain() {
        Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
