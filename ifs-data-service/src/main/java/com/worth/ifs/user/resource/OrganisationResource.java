package com.worth.ifs.user.resource;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.springframework.hateoas.core.Relation;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

@Relation(value="organisation", collectionRelation="organisations")
public class OrganisationResource {
    private Long id;
    private String name;
    private String companyHouseNumber;
    private OrganisationSize organisationSize;
    private List<Long> processRoleIds = new ArrayList<>();
    private List<Long> applicationFinanceIds = new ArrayList<>();
    private List<Long> userIds = new ArrayList<>();

    public OrganisationResource() {
        /*default constructor*/
    }

    private List<Long> addressIds = new ArrayList<>();

    public OrganisationResource(Organisation organisation) {
        id=organisation.getId();
        name = organisation.getName();
        companyHouseNumber = organisation.getCompanyHouseNumber();
        organisationSize = organisation.getOrganisationSize();

        addressIds = simpleMap(organisation.getAddresses(), OrganisationAddress::getId);
        processRoleIds = simpleMap(organisation.getProcessRoles(), ProcessRole::getId);
        applicationFinanceIds = simpleMap(organisation.getApplicationFinances(), ApplicationFinance::getId);
        userIds = simpleMap(organisation.getUsers(), User::getId);
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

    public List<Long> getProcessRoleIds() {
        return processRoleIds;
    }

    public void setProcessRoleIds(List<Long> processRoleIds) {
        this.processRoleIds = processRoleIds;
    }

    public List<Long> getApplicationFinanceIds() {
        return applicationFinanceIds;
    }

    public void setApplicationFinanceIds(List<Long> applicationFinanceIds) {
        this.applicationFinanceIds = applicationFinanceIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getAddressIds() {
        return addressIds;
    }

    public void setAddressIds(List<Long> addressIds) {
        this.addressIds = addressIds;
    }

    public String getCompanyHouseNumber() {
        return companyHouseNumber;
    }

    public void setCompanyHouseNumber(String companyHouseNumber) {
        this.companyHouseNumber = companyHouseNumber;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }
}
