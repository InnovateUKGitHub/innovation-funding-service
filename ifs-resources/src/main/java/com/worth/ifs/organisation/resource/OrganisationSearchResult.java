package com.worth.ifs.organisation.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.worth.ifs.address.resource.AddressResource;

/**
 * Resource object to store the company details, from the company house api.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationSearchResult implements Serializable{
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
    	List<String> parts = new ArrayList<>();
    	if(!StringUtils.isEmpty(organisationAddress.getAddressLine1())){
    		parts.add(organisationAddress.getAddressLine1());
    	}
    	if(!StringUtils.isEmpty(organisationAddress.getTown())){
    		parts.add(organisationAddress.getTown());
    	}
    	if(!StringUtils.isEmpty(organisationAddress.getPostcode())){
    		parts.add(organisationAddress.getPostcode());
    	}
    	return String.join(", ", parts);
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
