package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class EmployeesAndTurnover extends FinancialYearAccounts {

    @Column(columnDefinition = "double")
    private BigDecimal turnover;

    @Column(columnDefinition = "int(11)")
    private Long employees;

    public EmployeesAndTurnover() {
        super();
    }

    public EmployeesAndTurnover(EmployeesAndTurnover employeesAndTurnover) {
        this.employees = employeesAndTurnover.getEmployees();
        this.turnover = employeesAndTurnover.getTurnover();
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public Long getEmployees() {
        return employees;
    }

    public void setEmployees(Long employees) {
        this.employees = employees;
    }
}
