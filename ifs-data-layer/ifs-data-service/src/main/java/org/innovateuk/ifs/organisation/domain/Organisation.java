package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;
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

    @Column
    private String knowledgeBaseRegistrationNumber;

    @OneToMany(mappedBy = "organisation",
            cascade = CascadeType.ALL)
    private List<OrganisationAddress> addresses = new ArrayList<>();

    //Only used by queries.
    @OneToMany(mappedBy="organisationId")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy = "organisation")
    private List<InviteOrganisation> inviteOrganisations = new ArrayList<>();

    @Column
    private ZonedDateTime dateOfIncorporation;

    @OneToMany(mappedBy="organisation",cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SicCode> sicCodes = new ArrayList<>();

    @OneToMany(mappedBy="organisation",cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ExecutiveOfficer> executiveOfficers = new ArrayList<>();

    public Organisation() {
    }

    public Organisation(String name) {
        this.name = name;
    }

    public Organisation(String name, String companiesHouseNumber) {
        this.name = name;
        this.companiesHouseNumber = companiesHouseNumber;
    }
    public Organisation(String name, String companiesHouseNumber, ZonedDateTime dateOfIncorporation) {
        this.name = name;
        this.companiesHouseNumber = companiesHouseNumber;
        this.dateOfIncorporation = dateOfIncorporation;
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

    public String getKnowledgeBaseRegistrationNumber() {
        return knowledgeBaseRegistrationNumber;
    }

    public void setKnowledgeBaseRegistrationNumber(String knowledgeBaseRegistrationNumber) {
        this.knowledgeBaseRegistrationNumber = knowledgeBaseRegistrationNumber;
    }

    public ZonedDateTime getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(ZonedDateTime dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }

    public List<SicCode> getSicCode() {
        return sicCodes;
    }

    public void setSicCode(List<SicCode> sicCode) {
        this.sicCodes = sicCode;
    }

    public List<ExecutiveOfficer> getExecutiveOfficers() {
        return executiveOfficers;
    }

    public void setExecutiveOfficers(List<ExecutiveOfficer> executiveOfficers) {
        this.executiveOfficers = executiveOfficers;
    }

    public void addExecutiveOfficer(ExecutiveOfficer officer) {
        executiveOfficers.add(officer);
        officer.setOrganisation(this);
    }

    public void removeExecutiveOfficier(ExecutiveOfficer officer) {
        executiveOfficers.remove(officer);
        officer.setOrganisation(null);
    }

    public void addSicCode(SicCode sicCode) {
        sicCodes.add(sicCode);
        sicCode.setOrganisation(this);
    }

    public void removeSicCode(SicCode sicCode) {
        sicCodes.remove(sicCode);
        sicCode.setOrganisation(null);
    }


}
