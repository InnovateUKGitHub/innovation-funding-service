package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

public class KtpYearResource {

    private Integer year;
    private BigDecimal turnover;
    private BigDecimal preTaxProfit;
    private BigDecimal currentAssets;
    private BigDecimal liabilities;
    private BigDecimal shareholderValue;
    private BigDecimal loans;
    private Long employees;

    public KtpYearResource() {}

    public KtpYearResource(Integer year, BigDecimal turnover, BigDecimal preTaxProfit, BigDecimal currentAssets, BigDecimal liabilities, BigDecimal shareholderValue, BigDecimal loans, Long employees) {
        this.year = year;
        this.turnover = turnover;
        this.preTaxProfit = preTaxProfit;
        this.currentAssets = currentAssets;
        this.liabilities = liabilities;
        this.shareholderValue = shareholderValue;
        this.loans = loans;
        this.employees = employees;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public BigDecimal getPreTaxProfit() {
        return preTaxProfit;
    }

    public void setPreTaxProfit(BigDecimal preTaxProfit) {
        this.preTaxProfit = preTaxProfit;
    }

    public BigDecimal getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(BigDecimal currentAssets) {
        this.currentAssets = currentAssets;
    }

    public BigDecimal getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(BigDecimal liabilities) {
        this.liabilities = liabilities;
    }

    public BigDecimal getShareholderValue() {
        return shareholderValue;
    }

    public void setShareholderValue(BigDecimal shareholderValue) {
        this.shareholderValue = shareholderValue;
    }

    public BigDecimal getLoans() {
        return loans;
    }

    public void setLoans(BigDecimal loans) {
        this.loans = loans;
    }

    public Long getEmployees() {
        return employees;
    }

    public void setEmployees(Long employees) {
        this.employees = employees;
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
                .toHashCode();
    }
}
