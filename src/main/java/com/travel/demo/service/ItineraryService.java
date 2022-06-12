package com.travel.demo.service;

import com.travel.demo.client.PaymentClient;
import com.travel.demo.constans.exceptions.BusinessException;
import com.travel.demo.constans.exceptions.ExceptionCode;
import com.travel.demo.converter.ItineraryConverter;
import com.travel.demo.dto.request.PaymentRequestDto;
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

import com.travel.demo.constans.Data.PaymentStatus;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItineraryService {

	private final SettlementPaymentRepository paymentRepository;

	private final TravelManagementContractRepository contractRepository;

	private final PaymentClient paymentClient;

	private final SettlementRepository settlementRepository;

	@Transactional
	public PaymentCreateResponseDto requestPayment(String contractId, String settlementId) {
		TravelManagementContract contract = contractRepository.findById(Long.parseLong(contractId))
			.orElseThrow(() -> new BusinessException(ExceptionCode.CONTRACT_NOT_FOUND));

		Settlement unpaidSettlement = getUnpaidSettlement(settlementId, contract);
		String transactionNo = TransactionGenerator.generate(contractId);
		try {
			pay(unpaidSettlement, transactionNo);
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
			PaymentRequestDto.builder()
				.transactionNo(transactionNo)
				.amount(unpaidSettlement.getAmount())
				.build()
		);
	}

	private Settlement getUnpaidSettlement(String settlementId, TravelManagementContract contract) {
		return settlementRepository.findById(Long.parseLong(settlementId))
			.filter(s -> s.getContractId().equals(contract.getId())
				&& PaymentStatus.UNPAID.equals(s.getStatus()))
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







