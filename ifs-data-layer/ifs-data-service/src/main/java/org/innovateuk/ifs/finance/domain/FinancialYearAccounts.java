package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;

@MappedSuperclass
public abstract class FinancialYearAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public FinancialYearAccounts() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}