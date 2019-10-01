package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class EmployeesAndTurnover extends FinancialYearAccounts {

    private BigDecimal turnover;

    public EmployeesAndTurnover() {
        super();
    }

    public EmployeesAndTurnover(EmployeesAndTurnover employeesAndTurnover) {
        super(employeesAndTurnover);
        this.turnover = employeesAndTurnover.getTurnover();
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
}
