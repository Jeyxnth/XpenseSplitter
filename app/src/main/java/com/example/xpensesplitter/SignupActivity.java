package com.example.xpensesplitter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private Button btnSignup, goToLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignup = findViewById(R.id.btnSignup);
        goToLogin = findViewById(R.id.goToLogin);

        btnSignup.setOnClickListener(v -> registerUser());
        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {

                        // ✅ Update Firebase Auth display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task -> {
                                    // ✅ Store user data in Firestore
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    userData.put("email", email);

                                    db.collection("users").document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                                // Go to main screen
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
