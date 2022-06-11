package com.travel.demo.repository;

import com.travel.demo.entity.Settlement;
import com.travel.demo.entity.SettlementPayment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
