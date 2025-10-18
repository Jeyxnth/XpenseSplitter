package com.example.xpensesplitter.models;

import com.google.firebase.Timestamp;

public class Expense {
    private String id;        // Firestore document ID
    private String title;
    private double amount;
    private String addedBy;
    private Timestamp timestamp; // ✅ FIXED: use Timestamp instead of long

    public Expense() {}

    public Expense(String id, String title, double amount, String addedBy, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.addedBy = addedBy;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
