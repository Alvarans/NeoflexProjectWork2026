package com.example.deal.mapping;

import com.example.api.common.dto.ScoringDataDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.entity.StatementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoringDataMapper {
    @Mapping(target = "amount", source = "statement.appliedOffer.totalAmount")
    @Mapping(target = "term", source = "statement.appliedOffer.term")
    @Mapping(target = "firstName", source = "statement.client.firstName")
    @Mapping(target = "lastName", source = "statement.client.lastName")
    @Mapping(target = "middleName", source = "statement.client.middleName")
    @Mapping(target = "birthdate", source = "statement.client.birthDate")
    @Mapping(target = "passportSeries", source = "statement.client.passport.series")
    @Mapping(target = "passportNumber", source = "statement.client.passport.number")
    @Mapping(target = "isInsuranceEnabled", source = "statement.appliedOffer.isInsuranceEnabled")
    @Mapping(target = "isSalaryClient", source = "statement.appliedOffer.isSalaryClient")

    @Mapping(target = "gender", source = "request.gender")
    @Mapping(target = "passportIssueDate", source = "request.passportIssueDate")
    @Mapping(target = "passportIssueBranch", source = "request.passportIssueBrach")
    @Mapping(target = "maritalStatus", source = "request.maritalStatus")
    @Mapping(target = "dependentAmount", source = "request.dependentAmount")
    @Mapping(target = "employment", source = "request.employment")
    @Mapping(target = "accountNumber", source = "request.accountNumber")
    ScoringDataDto toScoringDataDto(StatementEntity statement, FinishRegistrationRequestDto request);
}
