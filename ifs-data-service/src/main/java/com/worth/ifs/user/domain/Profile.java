package com.worth.ifs.user.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.commons.util.AuditableEntity;
import com.worth.ifs.user.resource.BusinessType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @ManyToOne(targetEntity = Address.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "skills_areas")
    private String skillsAreas;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @ManyToOne(targetEntity = Contract.class, fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;

    private LocalDateTime contractSignedDate;

    public Profile() {
        // no-arg constructor
    }

    public void signContract(Contract contract, LocalDateTime signedDate) {
        if (contract == null) throw new NullPointerException("contract cannot be null");
        if (signedDate == null) throw new NullPointerException("signedDate cannot be null");
        this.contract = contract;
        this.contractSignedDate = signedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public LocalDateTime getContractSignedDate() {
        return contractSignedDate;
    }

    public void setContractSignedDate(LocalDateTime contractSignedDate) {
        this.contractSignedDate = contractSignedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Profile profile = (Profile) o;

        return new EqualsBuilder()
                .append(id, profile.id)
                .append(address, profile.address)
                .append(skillsAreas, profile.skillsAreas)
                .append(businessType, profile.businessType)
                .append(contract, profile.contract)
                .append(contractSignedDate, profile.contractSignedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(address)
                .append(skillsAreas)
                .append(businessType)
                .append(contract)
                .append(contractSignedDate)
                .toHashCode();
    }
}