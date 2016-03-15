package com.worth.ifs.finance.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class FinanceType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String description;

    //@OneToMany(mappedBy="financeType")
    //private List<Cost> cost = new ArrayList<>();

    public FinanceType() {
    }

    public FinanceType(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
