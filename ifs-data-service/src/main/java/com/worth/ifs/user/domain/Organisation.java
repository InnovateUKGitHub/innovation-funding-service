package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.user.resource.OrganisationSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * organisation defines database relations and a model to use client side and server side.
 */
@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String companyHouseNumber; // might start with zero, so use a string.
    @Enumerated(EnumType.STRING)
    private OrganisationSize organisationSize;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationType organisationType;

    @OneToMany(mappedBy="organisation")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @ManyToMany(mappedBy="organisations")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organisation",
            cascade = CascadeType.ALL)
    private List<OrganisationAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy="organisation")
    private List<InviteOrganisation> inviteOrganisations = new ArrayList<>();

    public Organisation() {
    	// no-arg constructor
    }

    public Organisation(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Organisation(Long id, String name, String companyHouseNumber, OrganisationSize organisationSize) {
        this.id = id;
        this.name = name;
        this.companyHouseNumber = companyHouseNumber;
        this.organisationSize = organisationSize;
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

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    public List<User> getUsers() {
        return users;
    }


    public String getCompanyHouseNumber() {
        return companyHouseNumber;
    }

    public void setCompanyHouseNumber(String companyHouseNumber) {
        this.companyHouseNumber = companyHouseNumber;
    }

    public List<OrganisationAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<OrganisationAddress> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address, AddressType addressType){
        OrganisationAddress organisationAddress = new OrganisationAddress(this, address, addressType);
        this.addresses.add(organisationAddress);
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }
}
