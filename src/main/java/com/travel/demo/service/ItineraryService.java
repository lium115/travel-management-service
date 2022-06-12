package com.travel.demo.service;

import com.travel.demo.client.PaymentClient;
import com.travel.demo.constans.exceptions.BusinessException;
import com.travel.demo.constans.exceptions.ExceptionCode;
import com.travel.demo.converter.ItineraryConverter;
import com.travel.demo.dto.request.PaymentCreateRequestDto;
import com.travel.demo.dto.response.PaymentCreateResponseDto;
import com.travel.demo.dto.response.PaymentResponseDto;
import com.travel.demo.entity.Settlement;
import com.travel.demo.entity.TravelManagementContract;
import com.travel.demo.repository.SettlementPaymentRepository;
import com.travel.demo.repository.SettlementRepository;
import com.travel.demo.repository.TravelManagementContractRepository;
import com.travel.demo.util.TransactionGenerator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.travel.demo.constans.Data.PaymentStatus;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItineraryService {

	private SettlementPaymentRepository paymentRepository;

	private TravelManagementContractRepository contractRepository;

	private PaymentClient paymentClient;

	private SettlementRepository settlementRepository;

	@Transactional
	public PaymentCreateResponseDto requestPayment(String contractId, String settlementId) {
		TravelManagementContract contract = contractRepository.findById(Long.parseLong(contractId))
			.orElseThrow(() -> new BusinessException(ExceptionCode.CONTRACT_NOT_FOUND));

		Settlement unpaidSettlement = getUnpaidSettlement(settlementId, contract);
		String transactionNo = TransactionGenerator.generate(contractId);

		pay(unpaidSettlement, transactionNo);

		try {
			return updatePaymentStatus(transactionNo, unpaidSettlement);
		} catch (Exception e) {
			RetryJob.execute((transactionNo1, settlement) -> {
				try {
					updatePaymentStatus(transactionNo1, settlement);
				} catch (Exception ex) {
					throw ex;
				}
			}, transactionNo, unpaidSettlement, 5);
		}
		return PaymentCreateResponseDto.builder().transactionNo(transactionNo).build();
	}

	private void pay(Settlement unpaidSettlement, String transactionNo) {
		paymentClient.payment(
			PaymentCreateRequestDto.builder()
				.transactionNo(transactionNo)
				.amount(unpaidSettlement.getAmount())
				.build()
		);
	}

	private Settlement getUnpaidSettlement(String settlementId, TravelManagementContract contract) {
		return Optional.ofNullable(contract.getSettlements()).orElse(List.of()).stream()
			.filter(s -> Long.parseLong(settlementId) == s.getId() &&
				PaymentStatus.UNPAID.equals(s.getStatus()))
			.findFirst()
			.orElseThrow(() -> new BusinessException(ExceptionCode.SETTLEMENT_INVALID));
	}

	private PaymentCreateResponseDto updatePaymentStatus(String transactionNo, Settlement settlement) {
		PaymentResponseDto paymentResponseDto = paymentClient.getPayment(transactionNo);
		paymentRepository.save(ItineraryConverter.convertTo(paymentResponseDto, settlement.getContractId()));
		Settlement unpaidSettlement = settlementRepository.findById(settlement.getId())
			.orElseThrow(() -> new BusinessException(ExceptionCode.SETTLEMENT_INVALID));
		unpaidSettlement.setStatus(PaymentStatus.PAID);
		settlementRepository.save(unpaidSettlement);
		return ItineraryConverter.ToPaymentCreateResponseDto(paymentResponseDto);
	}
}







