package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class OrganisationInternationalDetailsForm implements Serializable {

    @NotBlank(message = "{validation.standard.organisationname.required}")
    private String name;

    private String companyRegistrationNumber;

    @NotBlank(message = "{validation.standard.addressline1.required}")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "{validation.standard.town.required}")
    private String town;

    @NotBlank(message = "{validation.standard.country.required}")
    private String country;

    private String zipCode;

    public OrganisationInternationalDetailsForm() {
    }

    public OrganisationInternationalDetailsForm(String name, String companyRegistrationNumber, String addressLine1, String addressLine2, String town, String country, String zipCode) {
        this.name = name;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.town = town;
        this.country = country;
        this.zipCode = zipCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
