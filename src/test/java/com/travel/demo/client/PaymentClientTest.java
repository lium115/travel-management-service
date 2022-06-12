package com.travel.demo.client;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.demo.dto.response.PaymentResponseDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootTest
public class PaymentClientTest {

	private ClientAndServer server;
	public static String CONTENT_TYPE = "Content-Type";
	public static String CONTENT_TYPE_VALUE = "application/json; charset=utf-8";

	@Autowired
	PaymentClient paymentClient;

	@BeforeEach
	void startFakeServer() {
		server = ClientAndServer.startClientAndServer(8000);
	}

	@AfterEach
	void stopFakeServer() {
		server.stop();
	}

	@Test
	void should_get_payment_success() throws JsonProcessingException {
		// given
		String transactionNo = "t123";
		String paymentId = "p123";
		PaymentResponseDto responseBody = PaymentResponseDto.builder()
			.transactionNo(transactionNo)
			.paymentId(paymentId)
			.amount(BigDecimal.valueOf(3000))
			.expiredAt(new Date())
			.build();

		server
			.when(
				request()
					.withMethod("GET")
					.withPath("/payments/" + transactionNo)
			)
			.respond(
				response()
					.withStatusCode(200)
					.withHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
					.withBody(new ObjectMapper().writeValueAsString(responseBody))
			);
		PaymentResponseDto paymentResponseDto = paymentClient.getPayment(transactionNo);

		Assertions.assertEquals(paymentId, paymentResponseDto.getPaymentId());
		Assertions.assertEquals(responseBody.getAmount(), paymentResponseDto.getAmount());
		Assertions.assertEquals(responseBody.getTransactionNo(), paymentResponseDto.getTransactionNo());
		Assertions.assertEquals(responseBody.getExpiredAt().toString(), paymentResponseDto.getExpiredAt().toString());
	}
}
