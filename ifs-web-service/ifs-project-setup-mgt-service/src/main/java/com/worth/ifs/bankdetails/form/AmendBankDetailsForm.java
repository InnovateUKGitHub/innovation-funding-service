package com.worth.ifs.bankdetails.form;

public class AmendBankDetailsForm extends BankDetailsForm {

    private String registrationNumber;
    private String organisationName;

    public AmendBankDetailsForm() {
    }

    public AmendBankDetailsForm(String registrationNumber, String organisationName) {
        this.registrationNumber = registrationNumber;
        this.organisationName = organisationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
