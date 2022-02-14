package org.innovateuk.ifs.finance.resource;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

@Getter
@Setter
public class KtpYearResource {

    private Integer year;
    private BigDecimal turnover;
    private BigDecimal preTaxProfit;
    private BigDecimal currentAssets;
    private BigDecimal liabilities;
    private BigDecimal shareholderValue;
    private BigDecimal loans;
    private Long employees;
    private Long corporateGroupEmployees;

    public KtpYearResource() {}

    public KtpYearResource(Integer year, BigDecimal turnover, BigDecimal preTaxProfit, BigDecimal currentAssets, BigDecimal liabilities, BigDecimal shareholderValue, BigDecimal loans, Long employees, Long corporateGroupEmployees) {
        this.year = year;
        this.turnover = turnover;
        this.preTaxProfit = preTaxProfit;
        this.currentAssets = currentAssets;
        this.liabilities = liabilities;
        this.shareholderValue = shareholderValue;
        this.loans = loans;
        this.employees = employees;
        this.corporateGroupEmployees = corporateGroupEmployees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        KtpYearResource that = (KtpYearResource) o;

        return new EqualsBuilder()
                .append(year, that.year)
                .append(turnover, that.turnover)
                .append(preTaxProfit, that.preTaxProfit)
                .append(currentAssets, that.currentAssets)
                .append(liabilities, that.liabilities)
                .append(shareholderValue, that.shareholderValue)
                .append(loans, that.loans)
                .append(employees, that.employees)
                .append(corporateGroupEmployees, that.corporateGroupEmployees)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(year)
                .append(turnover)
                .append(preTaxProfit)
                .append(currentAssets)
                .append(liabilities)
                .append(shareholderValue)
                .append(loans)
                .append(employees)
                .append(corporateGroupEmployees)
                .toHashCode();
    }
}
