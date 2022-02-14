package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Form used to capture "Your organisation" information when a growth table is required.
 */
@Getter
@Setter
public class YourOrganisationKtpFinancialYearForm {

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Integer year;

    private BigDecimal turnover;

    private BigDecimal preTaxProfit;

    private BigDecimal currentAssets;

    private BigDecimal liabilities;

    private BigDecimal shareholderValue;

    private BigDecimal loans;

    @Min(value = 0, message = "{validation.ktp.financial.years.employees}")
    private Long employees;

    private Long corporateGroupEmployees;

    public YourOrganisationKtpFinancialYearForm() {}

    public YourOrganisationKtpFinancialYearForm(Integer year, BigDecimal turnover, BigDecimal preTaxProfit, BigDecimal currentAssets, BigDecimal liabilities, BigDecimal shareholderValue, BigDecimal loans, Long employees, Long corporateGroupEmployees) {
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

    /* view logic */
    public String getYearText() {
        if (year == 0) {
            return "Last year";
        } else {
            return "Year " + (year + 1);
        }
    }

    /* view logic */
    public String getPhase2YearText() {
        switch (year) {
            case 0:
                return "Latest period";
            case 1:
                return "Last audited year";
            case 2:
                return "Prior audited year";
            default:
                return "Year " + (year + 1);
        }
    }
}
