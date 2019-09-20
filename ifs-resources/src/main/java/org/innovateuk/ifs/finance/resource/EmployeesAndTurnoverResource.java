package org.innovateuk.ifs.finance.resource;


import java.math.BigDecimal;

public class EmployeesAndTurnoverResource extends FinancialYearAccountsResource {
    private BigDecimal turnover;

    @Override
    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
}