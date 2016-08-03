package com.worth.ifs.sil.experian.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AccountDetails extends SILBankDetails {
    private String companyName;
    private String registrationNumber;
    private String firstName = "NA";
    private String lastName = "NA";
    private Address address;

    public AccountDetails() {}

    public AccountDetails(String sortcode, String accountNumber, String companyName, String registrationNumber, Address address) {
        super(sortcode, accountNumber);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.address = address;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AccountDetails that = (AccountDetails) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(companyName, that.companyName)
                .append(registrationNumber, that.registrationNumber)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(companyName)
                .append(registrationNumber)
                .append(firstName)
                .append(lastName)
                .append(address)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AccountDetails{" +
                "companyName='" + companyName + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                '}';
    }
}
