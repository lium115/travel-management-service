package com.travel.demo.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.travel.demo.constans.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Settlement {
    @Id
    @GeneratedValue
    Long id;

    BigDecimal amount;

    Long contractId;

    Date createdAt;

    PaymentStatus status;
}
