package com.worth.ifs.sil.experian.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AccountDetails {
    private String sortcode;
    private String accountNumber;
    private String companyName;
    private String registrationNumber;
    private Address address;

    public AccountDetails(String sortcode, String accountNumber, String companyName, String registrationNumber, Address address) {
        this.sortcode = sortcode;
        this.accountNumber = accountNumber;
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.address = address;
    }

    public AccountDetails() {
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AccountDetails that = (AccountDetails) o;

        return new EqualsBuilder()
                .append(sortcode, that.sortcode)
                .append(accountNumber, that.accountNumber)
                .append(companyName, that.companyName)
                .append(registrationNumber, that.registrationNumber)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sortcode)
                .append(accountNumber)
                .append(companyName)
                .append(registrationNumber)
                .append(address)
                .toHashCode();
    }
}
