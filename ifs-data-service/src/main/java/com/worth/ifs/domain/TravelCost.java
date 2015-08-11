package com.worth.ifs.domain;

import javax.persistence.*;

@Entity
public class TravelCost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String purpose;
    private Integer numberOfTimes;
    private Double cost;

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public TravelCost(String purpose, Integer numberOfTimes, Double cost) {
        this.purpose = purpose;
        this.numberOfTimes = numberOfTimes;
        this.cost = cost;
    }

    public TravelCost(Long id, String purpose, Integer numberOfTimes, Double cost) {
        this.id = id;
        this.purpose = purpose;
        this.numberOfTimes = numberOfTimes;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public String getPurpose() {
        return purpose;
    }

    public Integer getNumberOfTimes() {
        return numberOfTimes;
    }

    public Double getCost() {
        return cost;
    }
}
