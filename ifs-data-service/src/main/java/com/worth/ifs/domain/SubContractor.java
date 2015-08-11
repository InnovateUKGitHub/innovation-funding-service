package com.worth.ifs.domain;

import javax.persistence.*;

@Entity
public class SubContractor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private String name;
    private String country;
    private String role;
    private Double cost;

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public SubContractor(Long id, String name, String country, String role, Double cost) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.role = role;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getRole() {
        return role;
    }

    public Double getCost() {
        return cost;
    }
}
