package com.example.deal.mapping;

import com.example.deal.dto.StatementDto;
import com.example.deal.entity.StatementEntity;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StatementMapper {
    StatementDto toStatementDto(StatementEntity statement);

    List<StatementDto> toStatementDtoList(List<StatementEntity> entities);

    // Конвертация для creationDate (LocalDateTime -> OffsetDateTime)
    default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    // Конвертация для signDate (LocalDateTime -> JsonNullable<OffsetDateTime>)
    default JsonNullable<OffsetDateTime> mapLocalDateTimeToJsonNullable(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return JsonNullable.undefined();
        }
        return JsonNullable.of(localDateTime.atOffset(ZoneOffset.UTC));
    }

    // Конвертация для sesCode (String -> JsonNullable<String>)
    default JsonNullable<String> mapStringToJsonNullable(String string) {
        if (string == null) {
            return JsonNullable.undefined();
        }
        return JsonNullable.of(string);
    }
}
