package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.Set;

@Entity
public class Conversation {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Set<MyAppUser> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<MyAppUser> participants) {
        this.participants = participants;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private boolean isGroup;

    @ManyToMany
    private Set<MyAppUser> participants;
}
