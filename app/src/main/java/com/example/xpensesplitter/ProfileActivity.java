package com.example.xpensesplitter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName;
    private TextView textEmail;
    private Button btnUpdateName, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editName = findViewById(R.id.editName);
        textEmail = findViewById(R.id.textEmail);
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Display current info
        textEmail.setText(user.getEmail());
        editName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");

        // Update name button
        btnUpdateName.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            updateUserName(newName);
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void updateUserName(String newName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Update display name in Firebase Auth
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Also update in Firestore
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", newName);

                    db.collection("users").document(user.getUid())
                            .update(updates)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(ProfileActivity.this, "Name updated successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ProfileActivity.this, "Firestore update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Auth update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
