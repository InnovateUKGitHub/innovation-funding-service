package org.innovateuk.ifs.finance.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//@Entity
public class FinanceType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String description;

    public FinanceType() {
    	// no-arg constructor
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
