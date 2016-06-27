package com.worth.ifs.bankdetail.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.project.domain.Project;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

/**
 * Entity for persisting Bank Details for organisations associated with a project.
 */
@Entity
public class BankDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sortCode;

    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "projectId" , referencedColumnName = "id")
    private Project project;

    @OneToOne
    @JoinColumn(name = "addressId", referencedColumnName = "id")
    private Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BankDetail that = (BankDetail) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(sortCode, that.sortCode)
                .append(accountNumber, that.accountNumber)
                .append(project, that.project)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(sortCode)
                .append(accountNumber)
                .append(project)
                .toHashCode();
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
