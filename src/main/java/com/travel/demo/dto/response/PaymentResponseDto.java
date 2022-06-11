package com.travel.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    String paymentId;
    String transactionNo;
    BigDecimal amount;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date expiredAt;
}
