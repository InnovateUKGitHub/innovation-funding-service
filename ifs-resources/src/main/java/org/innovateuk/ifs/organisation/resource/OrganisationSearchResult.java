package org.innovateuk.ifs.organisation.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource object to store the company details, from the companies house api.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationSearchResult implements Serializable{
    private String organisationSearchId;
    private String name;
    private AddressResource organisationAddress;
    private List<OrganisationSicCodeResource> organisationSicCodes;
    private List<OrganisationExecutiveOfficerResource> organisationExecutiveOfficers;
    private Map<String, Object> extraAttributes;
    private String organisationStatus;
    private String organisationAddressSnippet;

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
        if(!ObjectUtils.isEmpty(organisationAddress.getAddressLine1())){
            parts.add(organisationAddress.getAddressLine1());
        }
        if(!ObjectUtils.isEmpty(organisationAddress.getTown())){
            parts.add(organisationAddress.getTown());
        }
        if(!ObjectUtils.isEmpty(organisationAddress.getPostcode())){
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

    public List<OrganisationSicCodeResource> getOrganisationSicCodes() {
        return organisationSicCodes;
    }

    public void setOrganisationSicCodes(List<OrganisationSicCodeResource> organisationSicCodes) {
        this.organisationSicCodes = organisationSicCodes;
    }

    public List<OrganisationExecutiveOfficerResource> getOrganisationExecutiveOfficers() {
        return organisationExecutiveOfficers;
    }

    public void setOrganisationExecutiveOfficers(List<OrganisationExecutiveOfficerResource> organisationExecutiveOfficers) {
        this.organisationExecutiveOfficers = organisationExecutiveOfficers;
    }

    public Map<String, Object> getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    public String getOrganisationStatus() {
        return organisationStatus;
    }

    public void setOrganisationStatus(String organisationStatus) {
        this.organisationStatus = organisationStatus;
    }

    public void setOrganisationAddressSnippet(String organisationAddressSnippet) {
        this.organisationAddressSnippet = organisationAddressSnippet;
    }

    public String getOrganisationAddressSnippet() {
        if(!ObjectUtils.isEmpty(organisationAddressSnippet)){
            return organisationAddressSnippet;
        }
        return "";
    }

    @JsonIgnore
    public Boolean isOrganisationValidToDisplay() {
        if (organisationStatus !=null && !organisationStatus.isEmpty() && OrganisationStatusEnum.isOrganisationInvalidSatus(organisationStatus)) {
            return false;
        }
       return true;
    }
}
