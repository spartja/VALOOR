package com.uniremington.valoor.chatbot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uniremington.valoor.chatbot.entity.ChatSession;

import jakarta.persistence.LockModeType;

public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {

    Optional<ChatSession> findByIdAndUserId(String id, Integer userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ChatSession s where s.id = :id and s.userId = :userId")
    Optional<ChatSession> findOwnedForUpdate(
            @Param("id") String id,
            @Param("userId") Integer userId);
}
