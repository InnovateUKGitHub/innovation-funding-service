package org.innovateuk.ifs.registration.form;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.validation.constraints.NotBlank;

public class KnowledgeBaseCreateForm {

    private String name;

    private OrganisationTypeEnum organisationTypeEnum;

    private String identification;

    @NotBlank(message = "{validation.international.addressline1.required}")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "{validation.international.town.required}")
    private String town;

    @NotBlank(message = "{validation.international.country.required}")
    private String country;

    private String zipCode;

    public KnowledgeBaseCreateForm() {
    }

    public KnowledgeBaseCreateForm(String name, OrganisationTypeEnum organisationTypeEnum, String identification, String addressLine1, String addressLine2, String town, String country, String zipCode) {
        this.name = name;
        this.organisationTypeEnum = organisationTypeEnum;
        this.identification = identification;
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

    public OrganisationTypeEnum getOrganisationTypeEnum() {
        return organisationTypeEnum;
    }

    public void setOrganisationTypeEnum(OrganisationTypeEnum organisationTypeEnum) {
        this.organisationTypeEnum = organisationTypeEnum;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
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
