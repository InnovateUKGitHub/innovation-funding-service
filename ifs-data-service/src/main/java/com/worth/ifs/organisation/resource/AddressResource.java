package com.worth.ifs.organisation.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.organisation.domain.OrganisationAddress;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

public class AddressResource {
    private Long id;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String careOf;
    private String country;
    private String locality;
    private String poBox;
    private String postalCode;
    private String region;
    private List<Long> organisations = new ArrayList<>();

    public AddressResource() {
    }

    public AddressResource(String addressLine1, String addressLine2, String addressLine3, String careOf, String country, String locality, String po_box, String postal_code, String region) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.careOf = careOf;
        this.country = country;
        this.locality = locality;
        this.poBox = po_box;
        this.postalCode = postal_code;
        this.region = region;
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

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCareOf() {
        return careOf;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @JsonIgnore
    public String getCombinedString(){
        String[] location = new String[3];
        location[0] = getPostalCode();
        location[1] = getAddressLine1();
        location[2] = getLocality();
        return String.join(", ", location);

    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public List<Long> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<OrganisationAddress> organisations) {
        this.organisations = simpleMap(organisations, OrganisationAddress::getId);
    }


}
