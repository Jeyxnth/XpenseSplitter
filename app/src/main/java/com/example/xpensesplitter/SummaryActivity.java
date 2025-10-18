package com.example.xpensesplitter;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity {

    private TextView summaryText;
    private FirebaseFirestore db;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summaryText = findViewById(R.id.textSummary);
        db = FirebaseFirestore.getInstance();

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "No group ID provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSummary();
    }

    private void loadSummary() {
        db.collection("groups")
                .document(groupId)
                .collection("expenses")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        summaryText.setText("No expenses yet.");
                        return;
                    }

                    Map<String, Double> userTotals = new HashMap<>();
                    double total = 0;

                    for (DocumentSnapshot doc : querySnapshot) {
                        String addedBy = doc.getString("addedBy");
                        Double amount = doc.getDouble("amount");

                        if (addedBy == null || amount == null) continue;

                        userTotals.put(addedBy, userTotals.getOrDefault(addedBy, 0.0) + amount);
                        total += amount;
                    }

                    int memberCount = userTotals.size();
                    if (memberCount == 0) {
                        summaryText.setText("No valid members found.");
                        return;
                    }

                    double splitAmount = total / memberCount;

                    StringBuilder result = new StringBuilder();
                    result.append("💰 Total Expenses: ₹").append(total).append("\n");
                    result.append("Each person should pay: ₹").append(String.format("%.2f", splitAmount)).append("\n\n");

                    for (Map.Entry<String, Double> entry : userTotals.entrySet()) {
                        String user = entry.getKey();
                        double paid = entry.getValue();
                        double balance = paid - splitAmount;

                        if (balance > 0) {
                            result.append("🟢 ").append(user).append(" should receive ₹")
                                    .append(String.format("%.2f", balance)).append("\n");
                        } else if (balance < 0) {
                            result.append("🔴 ").append(user).append(" owes ₹")
                                    .append(String.format("%.2f", Math.abs(balance))).append("\n");
                        } else {
                            result.append("⚪ ").append(user).append(" is settled.\n");
                        }
                    }

                    summaryText.setText(result.toString());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading summary: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
