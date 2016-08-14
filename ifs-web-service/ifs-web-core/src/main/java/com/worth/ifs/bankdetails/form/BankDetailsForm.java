package com.worth.ifs.bankdetails.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

public class BankDetailsForm extends ProjectDetailsAddressForm {
    @NotEmpty(message="Please enter a sort code")
    @Pattern(regexp = "\\d{6}", message = "Please enter a valid sort code")
    private String sortCode;

    @NotEmpty(message="Please enter an account number")
    @Pattern(regexp = "\\d{8}", message = "Please enter a valid account number")
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
