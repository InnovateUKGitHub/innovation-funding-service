package com.worth.ifs.sil.experian.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This is used to perform validation on account number and sort code via SIL API.
 */
public class SILBankDetails {
    private String sortcode;
    private String accountNumber;

    public SILBankDetails() {}

    public SILBankDetails(String sortcode, String accountNumber) {
        this.sortcode = sortcode;
        this.accountNumber = accountNumber;
    }

    public String getSortcode() {
        return sortcode;
    }

    public void setSortcode(String sortcode) {
        this.sortcode = sortcode;
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

        SILBankDetails that = (SILBankDetails) o;

        return new EqualsBuilder()
                .append(sortcode, that.sortcode)
                .append(accountNumber, that.accountNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sortcode)
                .append(accountNumber)
                .toHashCode();
    }
}
