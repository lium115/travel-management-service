package com.travel.demo.api;

import com.travel.demo.dto.response.PaymentCreateResponseDto;
import com.travel.demo.service.ItineraryService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/travel-management-contracts")
@AllArgsConstructor
public class ItineraryController {

    private ItineraryService itineraryService;

    @PostMapping("/{contractId}/monthly-settlements/{settlementId}/payment")
    public PaymentCreateResponseDto requestPayment(
        @PathVariable("contractId") String contactId,
        @PathVariable("settlementId") String settlementId
    ) throws Exception {
        return itineraryService.requestPayment(contactId, settlementId);
    }
}
