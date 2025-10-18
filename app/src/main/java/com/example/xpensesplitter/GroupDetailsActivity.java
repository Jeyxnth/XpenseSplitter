package com.example.xpensesplitter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensesplitter.adapters.ExpenseAdapter;
import com.example.xpensesplitter.models.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends AppCompatActivity {

    private TextView textGroupName, textCreatedBy, textGroupCode;
    private Button buttonAddExpense, buttonViewSummary, btnDeleteGroup, buttonCopyCode, buttonBack;
    private RecyclerView recyclerExpenses;
    private FirebaseFirestore db;
    private String groupId, createdByEmail;
    private FirebaseUser currentUser;

    private List<Expense> expenseList = new ArrayList<>();
    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // UI elements
        textGroupName = findViewById(R.id.textGroupName);
        textCreatedBy = findViewById(R.id.textCreatedBy);
        textGroupCode = findViewById(R.id.textGroupCode);
        buttonCopyCode = findViewById(R.id.buttonCopyCode);
        buttonAddExpense = findViewById(R.id.buttonAddExpense);
        buttonViewSummary = findViewById(R.id.buttonViewSummary);
        btnDeleteGroup = findViewById(R.id.btnDeleteGroup);
        buttonBack = findViewById(R.id.buttonBack);
        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(expenseList, (expenseId, addedBy) -> {
            if (currentUser != null &&
                    (addedBy.equalsIgnoreCase(currentUser.getDisplayName()) ||
                            addedBy.equalsIgnoreCase(currentUser.getEmail()))) {
                confirmDeleteExpense(expenseId);
            } else {
                Toast.makeText(this, "You can only delete your own expenses", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerExpenses.setAdapter(expenseAdapter);

        groupId = getIntent().getStringExtra("groupId");
        String groupName = getIntent().getStringExtra("groupName");

        textGroupName.setText("Group: " + groupName);
        textGroupCode.setText("Code: " + groupId);

        loadGroupDetails();
        loadExpenses();

        // Copy group code
        buttonCopyCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Group Code", groupId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Group code copied!", Toast.LENGTH_SHORT).show();
        });

        // Add Expense
        buttonAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        // View Summary
        buttonViewSummary.setOnClickListener(v -> {
            Intent intent = new Intent(this, SummaryActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        // Delete Group
        btnDeleteGroup.setOnClickListener(v -> confirmDeleteGroup());

        // Back Button ✅
        buttonBack.setOnClickListener(v -> finish());

        // Initially hide delete group button
        btnDeleteGroup.setVisibility(android.view.View.GONE);
    }

    private void loadGroupDetails() {
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        createdByEmail = documentSnapshot.getString("createdBy");
                        String createdByName = documentSnapshot.getString("createdByName");

                        if (createdByName != null && !createdByName.isEmpty()) {
                            textCreatedBy.setText("Created by: " + createdByName);
                        } else if (createdByEmail != null) {
                            textCreatedBy.setText("Created by: " + createdByEmail);
                        } else {
                            textCreatedBy.setText("Created by: Unknown");
                        }

                        // ✅ Ensure email comparison is lowercase to avoid mismatch
                        if (currentUser != null && createdByEmail != null &&
                                createdByEmail.trim().equalsIgnoreCase(currentUser.getEmail().trim())) {
                            btnDeleteGroup.setVisibility(android.view.View.VISIBLE);
                        } else {
                            btnDeleteGroup.setVisibility(android.view.View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load group info", Toast.LENGTH_SHORT).show());
    }

    private void loadExpenses() {
        db.collection("groups").document(groupId).collection("expenses")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading expenses", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    expenseList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Expense exp = doc.toObject(Expense.class);
                            exp.setId(doc.getId());
                            expenseList.add(exp);
                        }
                    }
                    expenseAdapter.notifyDataSetChanged();
                });
    }

    private void confirmDeleteExpense(String expenseId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> deleteExpense(expenseId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteExpense(String expenseId) {
        db.collection("groups").document(groupId)
                .collection("expenses").document(expenseId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show());
    }

    private void confirmDeleteGroup() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Group")
                .setMessage("Are you sure you want to delete this group? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteGroup())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteGroup() {
        db.collection("groups").document(groupId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Group deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error deleting group: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
