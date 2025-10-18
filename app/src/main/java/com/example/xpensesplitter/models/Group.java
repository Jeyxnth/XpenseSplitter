package com.example.xpensesplitter.models;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private String createdBy;
    private List<String> members;

    // Firestore requires a public no-arg constructor
    public Group() {}

    public Group(String id, String name, String createdBy, List<String> members) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.members = members;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
}
