package org.innovateuk.ifs.project.bankdetails.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsAddressForm;

import javax.validation.constraints.Pattern;

/**
 * Form field model for the bank details content
 */
public class BankDetailsForm extends ProjectDetailsAddressForm {
    @Pattern(regexp = "\\d{6}", message = "{validation.standard.sortcode.format}")
    private String sortCode;

    @Pattern(regexp = "\\d{8}", message = "{validation.standard.accountnumber.format}")
    private String accountNumber;

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BankDetailsForm that = (BankDetailsForm) o;

        return new EqualsBuilder()
                .append(sortCode, that.sortCode)
                .append(accountNumber, that.accountNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sortCode)
                .append(accountNumber)
                .toHashCode();
    }
}
