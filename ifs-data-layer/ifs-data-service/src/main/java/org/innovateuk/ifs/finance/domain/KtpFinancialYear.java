package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class KtpFinancialYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;
    private BigDecimal turnover;
    private BigDecimal preTaxProfit;
    private BigDecimal currentAssets;
    private BigDecimal liabilities;
    private BigDecimal shareholderValue;
    private BigDecimal loans;
    private Long employees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ktpFinancialYearsId", referencedColumnName="id", nullable = false, updatable = false)
    private KtpFinancialYears ktpFinancialYears;

    KtpFinancialYear() {}

    public KtpFinancialYear(Integer year, KtpFinancialYears ktpFinancialYears) {
        this.year = year;
        this.ktpFinancialYears = ktpFinancialYears;
    }

    public KtpFinancialYear(Integer year, BigDecimal turnover, BigDecimal preTaxProfit, BigDecimal currentAssets, BigDecimal liabilities, BigDecimal shareholderValue, BigDecimal loans, Long employees, KtpFinancialYears ktpFinancialYears) {
        this(year, ktpFinancialYears);
        this.turnover = turnover;
        this.preTaxProfit = preTaxProfit;
        this.currentAssets = currentAssets;
        this.liabilities = liabilities;
        this.shareholderValue = shareholderValue;
        this.loans = loans;
        this.employees = employees;
    }

    public KtpFinancialYear(KtpFinancialYear year, KtpFinancialYears ktpFinancialYears) {
        this(year.getYear(), year.getTurnover(), year.getPreTaxProfit(), year.getCurrentAssets(), year.getLiabilities(), year.getShareholderValue(), year.getLoans(), year.getEmployees(), ktpFinancialYears);
    }

    public Long getId() {
        return id;
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

    public KtpFinancialYears getKtpFinancialYears() {
        return ktpFinancialYears;
    }

    public void setKtpFinancialYears(KtpFinancialYears ktpFinancialYears) {
        this.ktpFinancialYears = ktpFinancialYears;
    }
}
