package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints.LastFinancialYearEnd;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Form used to capture "Your organisation" information when a the competition is ktp.
 */
public class YourOrganisationKtpFinancialYearsForm {

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    @Valid
    private List<YourOrganisationKtpFinancialYearForm> years;

    private Long groupEmployees;

    @LastFinancialYearEnd(messageNotNull = "{validation.standard.mm.yyyy.format}",
            messagePastYearMonth = "{validation.standard.past.mm.yyyy.not.past.format}",
            messagePositiveYearMonth = "{validation.standard.mm.yyyy.format}")
    private YearMonth financialYearEnd;

    private YourOrganisationDetailsReadOnlyForm yourOrganisationDetailsReadOnlyForm;

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

//    @JsonIgnore
//    public String getOrganisationAddress() {
//        if (organisation.getAddresses() != null)
//        {
//            for(OrganisationAddressResource addressResource : organisation.getAddresses()) {
//                return addressResource.getAddress().getCombinedString();
//            }
//        }
//        return "";
//    }
//
//    @JsonIgnore
//    public String getOrganisationSICcodes() {
//        if (organisation.getSicCodes() != null)
//        {
//            String sicCode = "";
//            for(OrganisationSicCodeResource sicCodeResource : organisation.getSicCodes()) {
//                sicCode = String.join("/n", sicCodeResource.getSicCode());
//            }
//            return sicCode;
//        }
//        return "";
//    }

    @JsonIgnore
    public String getFinancialYearAndMonthString() {
       if (financialYearEnd != null) {
          return financialYearEnd.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " +
                   financialYearEnd.getYear();
       }
       return "";
    }

    public YourOrganisationDetailsReadOnlyForm getYourOrganisationDetailsReadOnlyForm() {
        return yourOrganisationDetailsReadOnlyForm;
    }

    public void setYourOrganisationDetailsReadOnlyForm(YourOrganisationDetailsReadOnlyForm yourOrganisationDetailsReadOnlyForm) {
        this.yourOrganisationDetailsReadOnlyForm = yourOrganisationDetailsReadOnlyForm;
    }
}