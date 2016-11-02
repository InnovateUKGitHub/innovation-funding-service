package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

/**
 * TODO DW - document this class
 */
public class ApplicationFinanceData {
    private ApplicationResource application;
    private OrganisationResource organisation;
    private UserResource user;

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setOrganisation(OrganisationResource organisation) {
        this.organisation = organisation;
    }

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public UserResource getUser() {
        return user;
    }
}
