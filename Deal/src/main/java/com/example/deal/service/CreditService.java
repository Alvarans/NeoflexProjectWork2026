package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.CreditStatus;
import com.example.deal.entity.CreditEntity;
import com.example.deal.mapping.CreditMapper;
import com.example.deal.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    public UUID createCredit(CreditDto creditDto) {
        CreditEntity creditEntity = creditMapper.toEntity(creditDto);
        creditEntity.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(creditEntity);
        return creditEntity.getCreditId();
    }
}
