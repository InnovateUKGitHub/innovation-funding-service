package com.worth.ifs.finance.resource;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;

import java.util.ArrayList;
import java.util.List;

public class ApplicationFinanceResource {
    Long id;
    private Long organisation;
    private Long application;
    private OrganisationSize organisationSize;
    private List<CostItem> costItems;

    public ApplicationFinanceResource(ApplicationFinance applicationFinance) {
        this.id = applicationFinance.getId();
        this.organisation = applicationFinance.getOrganisation().getId();
        this.application = applicationFinance.getApplication().getId();
        this.organisationSize = applicationFinance.getOrganisationSize();
        this.costItems = new ArrayList<>();
    }

    public ApplicationFinanceResource() {
    }

    public ApplicationFinanceResource(Long id, Long organisation, Long application, OrganisationSize organisationSize) {
        this.id = id;
        this.organisation = organisation;
        this.application = application;
        this.organisationSize = organisationSize;
    }

    public ApplicationFinanceResource(long id, Application application, Organisation organisation) {
        this.id = id;
        this.application = application.getId();
        this.organisation = organisation.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public Long getApplication() {
        return application;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public void setApplication(Long application) {
        this.application = application;
    }
}