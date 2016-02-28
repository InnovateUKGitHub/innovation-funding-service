package com.worth.ifs.organisation.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.resource.ResourceWithEmbeddeds;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource object to store the company details, from the company house api.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationSearchResult extends ResourceWithEmbeddeds implements Serializable{
    private String organisationSearchId;
    private String name;
    private AddressResource organisationAddress;
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
        locationString +=  ", "+ organisationAddress.getTown();
        locationString +=  ", "+ organisationAddress.getPostcode();
        return locationString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressResource getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(AddressResource organisationAddress) {
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
