package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class EmployeesAndTurnover extends FinancialYearAccounts {
    private BigDecimal turnover;

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
}
