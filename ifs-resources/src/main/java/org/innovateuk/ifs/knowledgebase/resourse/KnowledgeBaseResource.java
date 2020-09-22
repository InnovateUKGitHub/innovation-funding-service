package org.innovateuk.ifs.knowledgebase.resourse;

import org.innovateuk.ifs.address.resource.AddressResource;


public class KnowledgeBaseResource {
    private Long id;
    private String name;
    private KnowledgeBaseType type;
    private String registrationNumber;
    private AddressResource address;

    public KnowledgeBaseResource() {
    }

    public KnowledgeBaseResource(Long id, String name, KnowledgeBaseType type, String registrationNumber, AddressResource address) {
        this.id = id;
        this.name = name;
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

    public KnowledgeBaseType getType() {
        return type;
    }

    public void setType(KnowledgeBaseType type) {
        this.type = type;
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
