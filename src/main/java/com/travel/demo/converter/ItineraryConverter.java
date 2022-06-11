package com.travel.demo.converter;

import com.travel.demo.dto.response.PaymentCreateResponseDto;
import com.travel.demo.dto.response.PaymentResponseDto;
import com.travel.demo.entity.SettlementPayment;

import java.util.Date;

public class ItineraryConverter {
	public static SettlementPayment convertTo(PaymentResponseDto paymentResponseDto, Long contractId){
		return SettlementPayment.builder()
			.contractId(contractId)
			.transactionNo(paymentResponseDto.getTransactionNo())
			.expiredAt(paymentResponseDto.getExpiredAt())
			.createdAt(new Date())
			.build();
	}

	public static PaymentCreateResponseDto ToPaymentCreateResponseDto(PaymentResponseDto paymentResponseDto){
		return PaymentCreateResponseDto.builder()
			.paymentId(paymentResponseDto.getPaymentId())
			.transactionNo(paymentResponseDto.getTransactionNo())
			.expiredAt(paymentResponseDto.getExpiredAt())
			.amount(paymentResponseDto.getAmount())
			.build();
	}
}
