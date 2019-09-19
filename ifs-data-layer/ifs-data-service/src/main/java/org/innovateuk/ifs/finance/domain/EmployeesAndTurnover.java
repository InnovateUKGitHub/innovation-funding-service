package org.innovateuk.ifs.finance.domain;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class EmployeesAndTurnover extends CompanyFinances {
    private BigDecimal turnover;

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
}
