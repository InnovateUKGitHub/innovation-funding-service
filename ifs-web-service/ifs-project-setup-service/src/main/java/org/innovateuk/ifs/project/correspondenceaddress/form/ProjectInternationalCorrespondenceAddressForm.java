package org.innovateuk.ifs.project.correspondenceaddress.form;

import org.innovateuk.ifs.address.resource.AddressResource;

import javax.validation.constraints.NotBlank;

public class ProjectInternationalCorrespondenceAddressForm {

    @NotBlank(message = "{validation.international.addressline1.required}")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "{validation.international.town.required}")
    private String town;

    @NotBlank(message = "{validation.international.country.required}")
    private String country;

    private String zipCode;

    public ProjectInternationalCorrespondenceAddressForm(String addressLine1, String addressLine2, String town, String country, String zipCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.town = town;
        this.country = country;
        this.zipCode = zipCode;
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

    public AddressResource populate(AddressResource address) {
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.town = address.getTown();
        this.country = address.getCountry();
        this.zipCode = address.getPostcode();
        return address;
    }

    public AddressResource toAddress() {
        return new AddressResource(addressLine1, addressLine2, town, country, zipCode);
    }
}
