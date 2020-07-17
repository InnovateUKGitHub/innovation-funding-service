package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Form used to capture "Your organisation" information when a growth table is required.
 */
public class YourOrganisationKtpFinancialYearForm {

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Integer year;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal turnover;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal preTaxProfit;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal currentAssets;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal liabilities;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal shareholderValue;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal loans;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long employees;

    public YourOrganisationKtpFinancialYearForm() {}

    public YourOrganisationKtpFinancialYearForm(Integer year, BigDecimal turnover, BigDecimal preTaxProfit, BigDecimal currentAssets, BigDecimal liabilities, BigDecimal shareholderValue, BigDecimal loans, Long employees) {
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
}