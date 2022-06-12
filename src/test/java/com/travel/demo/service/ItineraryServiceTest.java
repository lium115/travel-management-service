package com.travel.demo.service;

import com.travel.demo.client.PaymentClient;
import com.travel.demo.constans.Data.PaymentStatus;
import com.travel.demo.constans.exceptions.BusinessException;
import com.travel.demo.constans.exceptions.ExceptionCode;
import com.travel.demo.dto.response.PaymentCreateResponseDto;
import com.travel.demo.dto.response.PaymentResponseDto;
import com.travel.demo.entity.Settlement;
import com.travel.demo.entity.TravelManagementContract;
import com.travel.demo.repository.SettlementPaymentRepository;
import com.travel.demo.repository.SettlementRepository;
import com.travel.demo.repository.TravelManagementContractRepository;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class ItineraryServiceTest {


	private ItineraryService itineraryService;

	@Mock
	private SettlementPaymentRepository paymentRepository;

	@Mock
	private TravelManagementContractRepository contractRepository;

	@Mock
	private PaymentClient paymentClient;

	@Mock
	private SettlementRepository settlementRepository;

	@Captor
	ArgumentCaptor<Settlement> settlementArgumentCaptor;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		itineraryService = new ItineraryService(
			paymentRepository, contractRepository, paymentClient, settlementRepository);
	}

	@Test
	void should_create_payment_request_success() {
		String contractId = "1";
		String settlementId = "11";
		String transactionNO = "t123";
		String paymentId = "p123";

		Settlement settlement = Settlement.builder()
			.amount(BigDecimal.valueOf(3000))
			.id(Long.parseLong(settlementId))
			.contractId(Long.parseLong(contractId))
			.status(PaymentStatus.UNPAID)
			.build();

		TravelManagementContract travelManagementContract = TravelManagementContract.builder()
			.id(Long.parseLong(contractId))
			.companyId("cid")
			.serviceAmount(BigDecimal.valueOf(1000))
			.settlements(List.of(settlement))
			.createdAt(new Date())
			.build();

		// given
		doReturn(Optional.of(travelManagementContract)).when(contractRepository).findById(1L);
		doReturn(Optional.of(settlement)).when(settlementRepository).findById(11L);
		doNothing().when(paymentClient).payment(any());
		doReturn(PaymentResponseDto.builder()
			.paymentId(paymentId)
			.amount(BigDecimal.valueOf(1000))
			.transactionNo(transactionNO)
			.expiredAt(new Date())
			.build()
		).when(paymentClient).getPayment(any());

		PaymentCreateResponseDto responseDto = itineraryService.requestPayment(contractId, settlementId);
		verify(settlementRepository).save(settlementArgumentCaptor.capture());

		assertEquals(paymentId, responseDto.getPaymentId());
		assertEquals(transactionNO, responseDto.getTransactionNo());
		assertEquals(PaymentStatus.PAID, settlementArgumentCaptor.getValue().getStatus());
	}

	@Test
	void should_create_payment_request_failed_when_contract_not_found() {
		String contractId = "1";
		String settlementId = "11";
		doReturn(Optional.empty()).when(contractRepository).findById(1L);

		assertThatThrownBy(() -> itineraryService
			.requestPayment(contractId, settlementId))
			.isInstanceOf(BusinessException.class)
			.extracting("code").isEqualTo(ExceptionCode.CONTRACT_NOT_FOUND.toString());
	}

	@Test
	void should_create_payment_request_failed_when_not_have_unpaid_settlement() {
		String contractId = "1";
		String settlementId = "11";

		Settlement settlement = Settlement.builder()
			.amount(BigDecimal.valueOf(3000))
			.id(Long.parseLong(settlementId))
			.contractId(Long.parseLong(contractId))
			.status(PaymentStatus.PAID)
			.build();

		TravelManagementContract travelManagementContract = TravelManagementContract.builder()
			.id(Long.parseLong(contractId))
			.companyId("cid")
			.serviceAmount(BigDecimal.valueOf(1000))
			.createdAt(new Date())
			.build();

		// given
		doReturn(Optional.of(travelManagementContract)).when(contractRepository).findById(1L);
		doReturn(Optional.of(settlement)).when(settlementRepository).findById(11L);

		assertThatThrownBy(() -> itineraryService
			.requestPayment(contractId, settlementId))
			.isInstanceOf(BusinessException.class)
			.extracting("code").isEqualTo(ExceptionCode.SETTLEMENT_INVALID.toString());
	}


	@Test
	void should_create_payment_request_failed_when_settlement_not_found() {
		String contractId = "1";
		String settlementId = "11";

		TravelManagementContract travelManagementContract = TravelManagementContract.builder()
			.id(Long.parseLong(contractId))
			.companyId("cid")
			.serviceAmount(BigDecimal.valueOf(1000))
			.createdAt(new Date())
			.build();

		// given
		doReturn(Optional.of(travelManagementContract)).when(contractRepository).findById(1L);
		doReturn(Optional.empty()).when(settlementRepository).findById(11L);

		assertThatThrownBy(() -> itineraryService
			.requestPayment(contractId, settlementId))
			.isInstanceOf(BusinessException.class)
			.extracting("code").isEqualTo(ExceptionCode.SETTLEMENT_INVALID.toString());
	}

	@Test
	void should_create_payment_request_failed_when_settlement_not_found_in_contract() {
		String contractId = "1";
		String settlementId = "11";

		Settlement settlement = Settlement.builder()
			.amount(BigDecimal.valueOf(3000))
			.id(Long.parseLong(settlementId))
			.contractId(Long.parseLong(contractId)+2)
			.status(PaymentStatus.UNPAID)
			.build();

		TravelManagementContract travelManagementContract = TravelManagementContract.builder()
			.id(Long.parseLong(contractId))
			.companyId("cid")
			.serviceAmount(BigDecimal.valueOf(1000))
			.createdAt(new Date())
			.build();

		// given
		doReturn(Optional.of(travelManagementContract)).when(contractRepository).findById(1L);
		doReturn(Optional.of(settlement)).when(settlementRepository).findById(11L);

		assertThatThrownBy(() -> itineraryService
			.requestPayment(contractId, settlementId))
			.isInstanceOf(BusinessException.class)
			.extracting("code").isEqualTo(ExceptionCode.SETTLEMENT_INVALID.toString());
	}
}
