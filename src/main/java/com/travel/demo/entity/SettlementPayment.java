package com.travel.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPayment {
	@Id
	@GeneratedValue
	Long id;

	Long contractId;

	String transactionNo;

	BigDecimal amount;

	Date createdAt;

	Date expiredAt;
}
