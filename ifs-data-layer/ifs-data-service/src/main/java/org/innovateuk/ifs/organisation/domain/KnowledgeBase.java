package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class KnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String registrationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationType organisationType;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    public KnowledgeBase() {
        // for ORM
    }

    public KnowledgeBase(Long id, String name, String registrationNumber, OrganisationType organisationType, Address address) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.organisationType = organisationType;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public OrganisationTypeEnum getOrganisationTypeEnum() {
        return OrganisationTypeEnum.getFromId(getOrganisationType().getId());
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
