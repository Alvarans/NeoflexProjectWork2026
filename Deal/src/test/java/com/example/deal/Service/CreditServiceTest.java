package com.example.deal.Service;

import com.example.api.common.dto.CreditDto;
import com.example.deal.dto.CreditStatus;
import com.example.deal.entity.CreditEntity;
import com.example.deal.mapping.CreditMapper;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.CreditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditServiceTest {
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private CreditMapper creditMapper;
    @InjectMocks
    private CreditService creditService;

    @Test
    void testCreateCredit_Success() {
        CreditDto creditDto = new CreditDto();
        CreditEntity creditEntity = new CreditEntity();
        UUID generatedId = UUID.randomUUID();
        creditEntity.setCreditId(generatedId);

        when(creditMapper.toEntity(creditDto)).thenReturn(creditEntity);
        when(creditRepository.save(creditEntity)).thenReturn(creditEntity);

        UUID resultId = creditService.createCredit(creditDto);

        assertEquals(generatedId, resultId);
        assertEquals(CreditStatus.CALCULATED, creditEntity.getCreditStatus());
        verify(creditRepository, times(1)).save(creditEntity);
        verify(creditMapper, times(1)).toEntity(creditDto);
    }
}
