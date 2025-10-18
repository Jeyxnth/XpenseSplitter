package com.example.xpensesplitter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText inputExpenseTitle, inputExpenseAmount;
    private Button buttonSaveExpense;
    private FirebaseFirestore db;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        db = FirebaseFirestore.getInstance();
        inputExpenseTitle = findViewById(R.id.inputExpenseTitle);
        inputExpenseAmount = findViewById(R.id.inputExpenseAmount);
        buttonSaveExpense = findViewById(R.id.buttonSaveExpense);

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "No group ID found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String title = inputExpenseTitle.getText().toString().trim();
        String amountStr = inputExpenseAmount.getText().toString().trim();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("amount", amount);
        expense.put("addedBy", user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
        expense.put("timestamp", FieldValue.serverTimestamp());

        db.collection("groups")
                .document(groupId)
                .collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error adding expense: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
