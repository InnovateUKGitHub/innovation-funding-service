package com.worth.ifs.organisation.resource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.worth.ifs.organisation.domain.Address;

/**
 * Resource object to store the company details, from the company house api.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationSearchResult implements Serializable{
    private String organisationSearchId;
    private String name;
    private Address organisationAddress;
    private Map<String, Object> extraAttributes;

    public OrganisationSearchResult(String id, String name) {
        this.name = name;
        this.organisationSearchId = id;
        extraAttributes = new HashMap<>();
    }

    public OrganisationSearchResult() {
        extraAttributes = new HashMap<>();
    }

    @JsonIgnore
    public String getLocation() {
        String locationString = "";
        locationString +=  organisationAddress.getAddressLine1();
        locationString +=  ", "+ organisationAddress.getLocality();
        locationString +=  ", "+ organisationAddress.getPostalCode();
        return locationString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(Address organisationAddress) {
        this.organisationAddress = organisationAddress;
    }


    public String getOrganisationSearchId() {
        return organisationSearchId;
    }

    public void setOrganisationSearchId(String organisationSearchId) {
        this.organisationSearchId = organisationSearchId;
    }

    public Map<String, Object> getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }
}
