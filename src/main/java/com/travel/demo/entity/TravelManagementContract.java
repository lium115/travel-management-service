package com.travel.demo.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelManagementContract {
    @Id
    Long id;

    BigDecimal serviceAmount;

    String companyId;

    Date createdAt;

    @OneToMany
    List<Settlement> settlements;
}
