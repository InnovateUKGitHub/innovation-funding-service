package org.innovateuk.ifs.organisation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.application.domain.Application;

import javax.persistence.*;
import javax.validation.Valid;

/**
 * Resource object to store the address details, from the company, from the companies house api.
 */
@Entity
@Table(name = "organisation_address", uniqueConstraints = {@UniqueConstraint(columnNames = {"organisation_id", "application_id", "address_type_id"})})
public class OrganisationApplicationAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;
    @Valid
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_type_id", referencedColumnName = "id")
    private AddressType addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    public OrganisationApplicationAddress(Organisation organisation, Application application, Address address, AddressType addressType) {
        this.organisation = organisation;
        this.application = application;
        this.address = address;
        this.addressType = addressType;
    }

    public OrganisationApplicationAddress() {
        // no-arg constructor
    }

    @JsonIgnore
    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
