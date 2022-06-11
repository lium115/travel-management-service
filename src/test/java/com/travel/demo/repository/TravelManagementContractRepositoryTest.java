package com.travel.demo.repository;

import com.travel.demo.constans.PaymentStatus;
import com.travel.demo.entity.Settlement;
import com.travel.demo.entity.TravelManagementContract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TravelManagementContractRepositoryTest {

    @Autowired
    TravelManagementContractRepository contractRepository;

    @Autowired
    SettlementRepository settlementRepository;

    @Test
    void should_find_by_id() {
        // given
        Long contractId = 1L;
        Long settlementId = 2L;

        contractRepository.save(TravelManagementContract.builder()
                .id(contractId)
                .companyId("cid")
                .serviceAmount(BigDecimal.valueOf(1000))
                .createdAt(new Date())
                .build());

        settlementRepository.save(Settlement.builder()
            .id(settlementId)
            .contractId(contractId)
            .status(PaymentStatus.PAID)
            .createdAt(new Date())
            .amount(BigDecimal.valueOf(1000))
            .build());

        // when
        Optional<TravelManagementContract> contractOptional = contractRepository.findById(contractId);
        // then
        Assertions.assertTrue(contractOptional.isPresent());
        Assertions.assertEquals(contractId, contractOptional.get().getId());
        Assertions.assertEquals(1, contractOptional.get().getSettlements().size());
        Assertions.assertEquals(PaymentStatus.PAID, contractOptional.get().getSettlements().get(0).getStatus());
    }
}
