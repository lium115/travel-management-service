package com.travel.demo.repository;

import com.travel.demo.constans.Data.PaymentStatus;
import com.travel.demo.entity.Settlement;

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
public class SettlementRepositoryTest {

	@Autowired
	SettlementRepository settlementRepository;

	@Test
	void should_find_by_id() {
		// given
		Long id = 2L;
		Long contractId = 1L;

		settlementRepository.save(Settlement.builder()
			.id(id)
			.contractId(contractId)
			.status(PaymentStatus.PAID)
			.createdAt(new Date())
			.amount(BigDecimal.valueOf(1000))
			.build());

		// when
		Optional<Settlement> settlementOptional = settlementRepository.findById(id);

		// then
		Assertions.assertTrue(settlementOptional.isPresent());
		Assertions.assertEquals(contractId, settlementOptional.get().getContractId());
		Assertions.assertEquals(PaymentStatus.PAID, settlementOptional.get().getStatus());
	}
}
