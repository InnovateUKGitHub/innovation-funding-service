package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints.LastFinancialYearEnd;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.validation.constraints.NotNull;
import java.time.YearMonth;
import java.util.List;

/**
 * Form used to capture "Your organisation" information when a the competition is ktp.
 */
public class YourOrganisationKtpFinancialYearsForm {

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    private List<YourOrganisationKtpFinancialYearForm> years;

    private Long groupEmployees;

    @LastFinancialYearEnd(messageNotNull = "{validation.standard.mm.yyyy.format}",
            messagePastYearMonth = "{validation.standard.past.mm.yyyy.not.past.format}",
            messagePositiveYearMonth = "{validation.standard.mm.yyyy.format}")
    private YearMonth financialYearEnd;

    public YourOrganisationKtpFinancialYearsForm() {
    }

    public YourOrganisationKtpFinancialYearsForm(OrganisationSize organisationSize, List<YourOrganisationKtpFinancialYearForm> years, Long groupEmployees, YearMonth financialYearEnd) {
        this.organisationSize = organisationSize;
        this.years = years;
        this.groupEmployees = groupEmployees;
        this.financialYearEnd = financialYearEnd;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public List<YourOrganisationKtpFinancialYearForm> getYears() {
        return years;
    }

    public void setYears(List<YourOrganisationKtpFinancialYearForm> years) {
        this.years = years;
    }

    public Long getGroupEmployees() {
        return groupEmployees;
    }

    public void setGroupEmployees(Long groupEmployees) {
        this.groupEmployees = groupEmployees;
    }

    public YearMonth getFinancialYearEnd() {
        return financialYearEnd;
    }

    public void setFinancialYearEnd(YearMonth financialYearEnd) {
        this.financialYearEnd = financialYearEnd;
    }
}