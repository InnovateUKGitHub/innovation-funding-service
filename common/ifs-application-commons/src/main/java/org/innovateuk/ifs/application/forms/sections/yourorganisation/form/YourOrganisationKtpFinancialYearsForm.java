package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints.LastFinancialYearEnd;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Form used to capture "Your organisation" information when a the competition is ktp.
 */
@Getter
@Setter
public class YourOrganisationKtpFinancialYearsForm {

    private boolean ktpPhase2Enabled;

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    @Valid
    private List<YourOrganisationKtpFinancialYearForm> years;

    private Long groupEmployees;

    private Boolean hasAdditionalInfoSection;
    private String additionalInfo;

    @LastFinancialYearEnd(messageNotNull = "{validation.standard.mm.yyyy.format}",
            messagePastYearMonth = "{validation.standard.past.mm.yyyy.not.past.format}",
            messagePositiveYearMonth = "{validation.standard.mm.yyyy.format}")
    private YearMonth financialYearEnd;

    public YourOrganisationKtpFinancialYearsForm() {
    }

    public YourOrganisationKtpFinancialYearsForm(boolean ktpPhase2Enabled, OrganisationSize organisationSize, List<YourOrganisationKtpFinancialYearForm> years, boolean hasAdditionalInfoSection, String additionalInfo, Long groupEmployees, YearMonth financialYearEnd) {
        this.ktpPhase2Enabled = ktpPhase2Enabled;
        this.organisationSize = organisationSize;
        this.years = years;
        this.hasAdditionalInfoSection = hasAdditionalInfoSection;
        this.additionalInfo = additionalInfo;
        this.groupEmployees = groupEmployees;
        this.financialYearEnd = financialYearEnd;
    }

    @JsonIgnore
    public String getFinancialYearAndMonthString() {
        String financialYearAndMonthString = "";
       if (financialYearEnd != null) {
           financialYearAndMonthString = financialYearEnd.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " +
                   financialYearEnd.getYear();
       }
       return financialYearAndMonthString;
    }

}