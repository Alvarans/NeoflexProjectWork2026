package com.example.deal.entity;

import com.example.deal.dto.ApplicationStatus;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.StatusHistoryRecord;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "statement")
public class StatementEntity {
    @Id
    @Column(name = "statement_id")
    private UUID statementId;

    @OneToOne()
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private ClientEntity client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "credit_id")
    private CreditEntity credit;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON) // Магическая аннотация для JSONB
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatusHistoryRecord> statusHistory;
}
