package com.example.xpensesplitter.utils;

import java.text.DecimalFormat;
import java.util.*;

public class SettlementUtils {

    private static final DecimalFormat df = new DecimalFormat("₹#,##0.00");

    public static String generateSummary(Map<String, Double> paidMap) {
        if (paidMap == null || paidMap.isEmpty()) {
            return "No expenses to summarize.";
        }

        double total = 0;
        for (double v : paidMap.values()) total += v;
        double share = total / paidMap.size();

        StringBuilder summary = new StringBuilder();
        summary.append("💰 Total Spent: ").append(df.format(total)).append("\n\n");

        for (Map.Entry<String, Double> entry : paidMap.entrySet()) {
            summary.append(entry.getKey()).append(" paid ").append(df.format(entry.getValue())).append("\n");
        }

        summary.append("\n⚖️ Each person owes ").append(df.format(share)).append("\n\n");
        summary.append("💳 Balances:\n");

        Map<String, Double> balances = new HashMap<>();
        for (Map.Entry<String, Double> e : paidMap.entrySet()) {
            balances.put(e.getKey(), round(e.getValue() - share));
        }

        List<Member> creditors = new ArrayList<>();
        List<Member> debtors = new ArrayList<>();

        for (Map.Entry<String, Double> e : balances.entrySet()) {
            double bal = e.getValue();
            if (bal > 0.01) creditors.add(new Member(e.getKey(), bal));
            else if (bal < -0.01) debtors.add(new Member(e.getKey(), bal));
        }

        // Sort for stability
        creditors.sort((a, b) -> Double.compare(b.amount, a.amount));
        debtors.sort((a, b) -> Double.compare(a.amount, b.amount));

        for (Member debtor : debtors) {
            double owed = -debtor.amount;
            for (Member creditor : creditors) {
                if (owed <= 0) break;
                if (creditor.amount <= 0) continue;

                double settle = Math.min(owed, creditor.amount);
                summary.append(debtor.name).append(" owes ")
                        .append(df.format(settle)).append(" to ").append(creditor.name).append("\n");

                creditor.amount -= settle;
                owed -= settle;
            }
        }

        return summary.toString();
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static class Member {
        String name;
        double amount;
        Member(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}
