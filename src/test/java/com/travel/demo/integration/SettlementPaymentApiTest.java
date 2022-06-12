package com.travel.demo.integration;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.travel.demo.client.PaymentClient;
import com.travel.demo.constans.Data.PaymentStatus;
import com.travel.demo.dto.response.PaymentResponseDto;
import com.travel.demo.entity.Settlement;
import com.travel.demo.entity.TravelManagementContract;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.demo.repository.SettlementPaymentRepository;
import com.travel.demo.repository.SettlementRepository;
import com.travel.demo.repository.TravelManagementContractRepository;
import com.travel.demo.service.ItineraryService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SettlementPaymentApiTest {
	public static String CONTENT_TYPE = "Content-Type";
	public static String CONTENT_TYPE_VALUE = "application/json; charset=utf-8";

	@Autowired
	private MockMvc mvc;

	private ClientAndServer server;

	@Autowired
	private TravelManagementContractRepository contractRepository;

	@Autowired
	private SettlementRepository settlementRepository;

	@Autowired
	private SettlementPaymentRepository paymentRepository;


	@BeforeEach
	void init() {
		server = ClientAndServer.startClientAndServer(8000);
	}

	@AfterEach
	void restore() {
		server.stop();
	}

	@Test
	void should_create_payment_request() throws Exception {
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
			.createdAt(new Date())
			.build();

		contractRepository.save(travelManagementContract);
		settlementRepository.save(settlement);

		PaymentResponseDto responseBody = PaymentResponseDto.builder()
			.transactionNo(transactionNO)
			.paymentId(paymentId)
			.amount(BigDecimal.valueOf(3000))
			.expiredAt(new Date())
			.build();

		server
			.when(
				request()
					.withMethod("POST")
					.withPath("/payments")
			)
			.respond(
				response()
					.withStatusCode(200)
			);

		server
			.when(
				request()
					.withMethod("GET")
			)
			.respond(
				response()
					.withStatusCode(200)
					.withHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
					.withBody(new ObjectMapper().writeValueAsString(responseBody))
			);
		// when
		mvc.perform(
				post("/travel-management-contracts/1/settlements/11/payment").contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentId").value(paymentId))
			.andExpect(jsonPath("$.transactionNo").value(transactionNO))
			.andExpect(jsonPath("$.amount").value(3000d));
	}
}
