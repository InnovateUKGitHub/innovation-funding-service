package com.worth.ifs.domain;

import javax.persistence.*;

@Entity
public class Materials {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    private String item;
    private Integer quantity;
    private Integer costPerItem;

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public Materials(long id, String item, int quantity, int costPerItem) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.costPerItem = costPerItem;
    }

    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getCostPerItem() {
        return costPerItem;
    }
}
