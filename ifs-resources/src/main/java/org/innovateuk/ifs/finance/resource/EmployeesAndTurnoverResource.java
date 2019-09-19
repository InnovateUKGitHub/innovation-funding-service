package org.innovateuk.ifs.finance.resource;


import java.math.BigDecimal;

public class EmployeesAndTurnoverResource extends CompanyFinancesResource {
    private BigDecimal turnover;

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
}