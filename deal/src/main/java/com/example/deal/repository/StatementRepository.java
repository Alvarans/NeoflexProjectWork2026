package com.example.deal.repository;

import com.example.deal.entity.StatementEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<StatementEntity, UUID> {
    Optional<StatementEntity> findByStatementId(UUID statementId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "10000") // Защита от "вечного" лока
    })
    @Query("select statement from StatementEntity statement where statement.statementId = :statementId")
    Optional<StatementEntity> findByStatementIdForUpdate(
            @Param("statementId") UUID statementId
    );
}
