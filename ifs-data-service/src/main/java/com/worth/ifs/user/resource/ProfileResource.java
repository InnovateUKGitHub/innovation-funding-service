package com.worth.ifs.user.resource;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Profile Data Transfer Object
 */
public class ProfileResource {
    private Long id;
    private UserResource user;
    private AddressResource address;
    private String skillsAreas;
    private BusinessType businessType;
    private ContractResource contract;
    private LocalDateTime contractSignedDate;

    public ProfileResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
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

    public ContractResource getContract() {
        return contract;
    }

    public void setContract(ContractResource contract) {
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProfileResource that = (ProfileResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user, that.user)
                .append(address, that.address)
                .append(skillsAreas, that.skillsAreas)
                .append(businessType, that.businessType)
                .append(contract, that.contract)
                .append(contractSignedDate, that.contractSignedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(user)
                .append(address)
                .append(skillsAreas)
                .append(businessType)
                .append(contract)
                .append(contractSignedDate)
                .toHashCode();
    }
}
