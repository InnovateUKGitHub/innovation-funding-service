package com.worth.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

/**
 * Profile Contract Data Transfer Object
 */
public class ProfileContractResource {

    private Long user;
    private ContractResource contract;
    private boolean currentAgreement;
    private LocalDateTime contractSignedDate;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public ContractResource getContract() {
        return contract;
    }

    public void setContract(ContractResource contract) {
        this.contract = contract;
    }

    public boolean isCurrentAgreement() {
        return currentAgreement;
    }

    public void setCurrentAgreement(boolean currentAgreement) {
        this.currentAgreement = currentAgreement;
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

        ProfileContractResource that = (ProfileContractResource) o;

        return new EqualsBuilder()
                .append(currentAgreement, that.currentAgreement)
                .append(user, that.user)
                .append(contract, that.contract)
                .append(contractSignedDate, that.contractSignedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(contract)
                .append(currentAgreement)
                .append(contractSignedDate)
                .toHashCode();
    }
}