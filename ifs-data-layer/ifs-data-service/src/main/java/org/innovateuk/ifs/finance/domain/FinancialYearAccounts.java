package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;

@MappedSuperclass
public abstract class FinancialYearAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long employees;

    public FinancialYearAccounts() {
    }

    public FinancialYearAccounts(FinancialYearAccounts accounts) {
        this.employees = accounts.getEmployees();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployees() {
        return employees;
    }

    public void setEmployees(Long employees) {
        this.employees = employees;
    }
}