package com.example.xpensesplitter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JoinGroupActivity extends AppCompatActivity {

    private EditText inputGroupCode;
    private Button buttonJoin;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        inputGroupCode = findViewById(R.id.inputGroupCode);
        buttonJoin = findViewById(R.id.buttonJoinGroup);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buttonJoin.setOnClickListener(v -> joinGroup());
    }

    private void joinGroup() {
        String groupCode = inputGroupCode.getText().toString().trim();
        if (groupCode.isEmpty()) {
            Toast.makeText(this, "Enter group code!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("groups").document(groupCode).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> members = (List<String>) documentSnapshot.get("members");

                        if (members != null && members.contains(userEmail)) {
                            Toast.makeText(this, "You’re already a member!", Toast.LENGTH_SHORT).show();
                        } else {
                            members.add(userEmail);
                            db.collection("groups").document(groupCode)
                                    .update("members", members)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Joined group successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(JoinGroupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error joining group: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Invalid group code!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
