package com.travel.demo.api;

import com.travel.demo.dto.response.PaymentCreateResponseDto;
import com.travel.demo.service.ItineraryService;
import com.travel.demo.util.TransactionGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.mockito.Mockito.doReturn;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItineraryController.class)
class ItineraryControllerTest {

	@MockBean
	private ItineraryService itineraryService;

	@Autowired
	private MockMvc mvc;

	private SimpleDateFormat dateFormatter;

	@BeforeEach
	void init() {
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Test
	void should_request_payment_success() throws Exception {
		// given
		String contractId = "MOCK_ID";
		String settlementId = "MOCK_SETTLEMENT_ID";
		String transactionNo = TransactionGenerator.generate(contractId);
		String paymentId = "paymentId";
		Date expiredAt = new Date();
		PaymentCreateResponseDto responseDto = PaymentCreateResponseDto.builder()
			.transactionNo(transactionNo)
			.paymentId(paymentId)
			.amount(BigDecimal.valueOf(1000))
			.expiredAt(expiredAt).build();

		doReturn(responseDto)
			.when(itineraryService).requestPayment(contractId, settlementId);

		mvc.perform(
				post("/travel-management-contracts/MOCK_ID/monthly-settlements/MOCK_SETTLEMENT_ID/payment").contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentId").value(paymentId))
			.andExpect(jsonPath("$.transactionNo").value(transactionNo))
			.andExpect(jsonPath("$.amount").value(1000d))
			.andExpect(jsonPath("$.expiredAt").value(dateFormatter.format(expiredAt)));
	}
}