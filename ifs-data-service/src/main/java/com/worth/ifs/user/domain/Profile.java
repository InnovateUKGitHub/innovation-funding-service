package com.worth.ifs.user.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.commons.util.AuditableEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * A {@link User}'s profile with their {@link Address}, skills areas and signed {@link Contract}.
 */
@Entity
public class Profile extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    @ManyToOne(targetEntity = Address.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "skills_areas")
    private String skillsAreas;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @ManyToOne(targetEntity = Contract.class)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;

    private LocalDateTime contractSignedDate;

    Profile() {
        // default constructor
    }

    public Profile(User user) {
        if (user == null) throw new NullPointerException("user cannot be null");
        // default constructor
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getSkillsAreas() {
        return skillsAreas;
    }

    public void setSkillsAreas(String skillsAreas) {
        this.skillsAreas = skillsAreas;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public Contract getContract() {
        return contract;
    }

    public LocalDateTime getContractSignedDate() {
        return contractSignedDate;
    }

    public void signContract(Contract contract, LocalDateTime signedDate) {
        if (contract == null) throw new NullPointerException("contract cannot be null");
        if (signedDate == null) throw new NullPointerException("signedDate cannot be null");
        this.contract = contract;
        this.contractSignedDate = signedDate;
    }

    void setUser(User user) {
        this.user = user;
    }
}