package com.example.demo.Repository;

import com.example.demo.Entity.Conversation;
import com.example.demo.Entity.MyAppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    List<Conversation> findByParticipantsContains(@Param("user1") MyAppUser user1, @Param("user2") MyAppUser user2);
}

