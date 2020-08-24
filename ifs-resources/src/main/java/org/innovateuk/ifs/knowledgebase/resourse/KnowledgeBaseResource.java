package org.innovateuk.ifs.knowledgebase.resourse;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;


public class KnowledgeBaseResource {
    private Long id;
    private String name;
    private Long organisationType;
    private String organisationTypeName;
    private String registrationNumber;
    private AddressResource address;

    public KnowledgeBaseResource() {
    }

    public KnowledgeBaseResource(Long id, String name, Long organisationType, String organisationTypeName, String registrationNumber, AddressResource address) {
        this.id = id;
        this.name = name;
        this.organisationType = organisationType;
        this.organisationTypeName = organisationTypeName;
        this.registrationNumber = registrationNumber;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }

    public void setOrganisationTypeName(String organisationTypeName) {
        this.organisationTypeName = organisationTypeName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }


    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }
}
