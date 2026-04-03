package com.example.deal.repository;

import com.example.deal.entity.StatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<StatementEntity, UUID> {
    StatementRepository findByStatementId(UUID statementId);
}
