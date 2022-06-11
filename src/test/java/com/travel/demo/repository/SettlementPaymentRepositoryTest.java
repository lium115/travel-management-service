package com.travel.demo.repository;

import com.travel.demo.entity.SettlementPayment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


import static org.apache.commons.lang3.time.DateUtils.addMinutes;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SettlementPaymentRepositoryTest {

	@Autowired
	SettlementPaymentRepository settlementPaymentRepository;

	@Test
	void should_find_by_id() {
		// given
		String id = "pid";
		Long contractId = 1L;

		settlementPaymentRepository.save(SettlementPayment.builder()
			.id(id)
			.transactionNo("t123")
			.contractId(contractId)
			.createdAt(new Date())
			.expiredAt(addMinutes(new Date(), 10))
			.amount(BigDecimal.valueOf(1000))
			.build());

		// when
		Optional<SettlementPayment> paymentOptional = settlementPaymentRepository.findById(id);
		// then
		Assertions.assertTrue(paymentOptional.isPresent());
		Assertions.assertEquals(contractId, paymentOptional.get().getContractId());
		Assertions.assertEquals("t123", paymentOptional.get().getTransactionNo());
	}
}
