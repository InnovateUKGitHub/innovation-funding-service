package com.worth.ifs.address.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class AddressResource {
    private Long id;

    @NotBlank
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    @NotBlank
    private String town;
    private String county;
    @NotBlank
    @Length(max = 9)
    private String postcode;
    private List<Long> organisations = new ArrayList<>();

    public AddressResource() {
    	// no-arg constructor
    }

    public AddressResource(String addressLine1, String addressLine2, String addressLine3, String town, String county, String postcode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @JsonIgnore
    public String getCombinedString(){
        String[] location = new String[3];
        location[0] = getPostcode();
        location[1] = getAddressLine1();
        location[2] = getTown();
        return String.join(", ", location);

    }

    @JsonIgnore
    public String getAsSingleLine(){
        if(getAddressLine1() == null && getTown() == null && getPostcode() == null){
            return "";
        }
        String[] location = new String[3];
        location[0] = getAddressLine1() == null ? "" : getAddressLine1();
        location[1] = getTown() == null ? "" : getTown();
        location[2] = getPostcode() == null ? "" : getPostcode();
        return String.join(", ", location);
    }

    public Long getId() {
        return id;
    }

    public List<Long> getOrganisations() {
        return organisations;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganisations(List<Long> organisations) {
        this.organisations = organisations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressResource that = (AddressResource) o;

        if(id != null) {
            if (!id.equals(that.id)) return false;
        }
        if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null) return false;
        if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null) return false;
        if (addressLine3 != null ? !addressLine3.equals(that.addressLine3) : that.addressLine3 != null) return false;
        if (town != null ? !town.equals(that.town) : that.town != null) return false;
        if (county != null ? !county.equals(that.county) : that.county != null) return false;
        if (postcode != null ? !postcode.equals(that.postcode) : that.postcode != null) return false;
        return organisations != null ? organisations.equals(that.organisations) : that.organisations == null;

    }
}
