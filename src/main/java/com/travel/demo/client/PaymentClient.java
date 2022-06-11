package com.travel.demo.client;

import com.travel.demo.dto.request.PaymentCreateRequestDto;
import com.travel.demo.dto.response.PaymentResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "paymentClient", url = "http://localhost:8000")
public interface PaymentClient {

    @RequestMapping(method = RequestMethod.POST, value = "/payments")
    public void payment(@RequestBody PaymentCreateRequestDto dto);

    @RequestMapping(method = RequestMethod.GET, value = "/payments/{transactionNo}")
    public PaymentResponseDto getPayment(@RequestParam(name = "transactionNo") String transactionNo);
}
