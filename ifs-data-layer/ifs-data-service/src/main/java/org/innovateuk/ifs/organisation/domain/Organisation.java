package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * organisation defines database relations and a model to use client side and server side.
 */
@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "companies_house_number")
    private String companiesHouseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationType organisationType;

    @Column(nullable = false)
    private boolean international;

    @Column(name = "international_registration_number")
    private String internationalRegistrationNumber;

    @OneToMany(mappedBy = "organisation",
            cascade = CascadeType.ALL)
    private List<OrganisationAddress> addresses = new ArrayList<>();

    //Only used by queries.
    @OneToMany(mappedBy="organisationId")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy = "organisation")
    private List<InviteOrganisation> inviteOrganisations = new ArrayList<>();

    public Organisation() {
    }

    public Organisation(String name) {
        this.name = name;
    }

    public Organisation(String name, String companiesHouseNumber) {
        this.name = name;
        this.companiesHouseNumber = companiesHouseNumber;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }

    public void setCompaniesHouseNumber(String companiesHouseNumber) {
        this.companiesHouseNumber = companiesHouseNumber;
    }

    public List<OrganisationAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<OrganisationAddress> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address, AddressType addressType) {
        OrganisationAddress organisationAddress = new OrganisationAddress(this, address, addressType);
        this.addresses.add(organisationAddress);
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

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }

    public String getInternationalRegistrationNumber() {
        return internationalRegistrationNumber;
    }

    public void setInternationalRegistrationNumber(String internationalRegistrationNumber) {
        this.internationalRegistrationNumber = internationalRegistrationNumber;
    }
}
