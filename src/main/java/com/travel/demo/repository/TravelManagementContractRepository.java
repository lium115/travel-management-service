package com.travel.demo.repository;

import com.travel.demo.entity.TravelManagementContract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelManagementContractRepository extends JpaRepository<TravelManagementContract, Long> {
}
