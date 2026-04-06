package com.example.deal.entity;

import com.example.deal.dto.ApplicationStatus;
import com.example.deal.dto.ChangeType;
import com.example.api.common.dto.LoanOfferDto;
import com.example.deal.dto.StatementStatusHistoryDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "statement")
public class StatementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();

    public void updateStatus(ApplicationStatus applicationStatus) {
        this.statusHistory.add(new StatementStatusHistoryDto(this.applicationStatus, OffsetDateTime.now(), ChangeType.MANUAL));
        this.applicationStatus = applicationStatus;
    }
}
