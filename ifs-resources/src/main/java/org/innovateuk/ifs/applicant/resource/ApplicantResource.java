package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantResource {

    private ProcessRoleResource processRole;

    private UserResource user;

    private OrganisationResource organisation;

    public ProcessRoleResource getProcessRole() {
        return processRole;
    }

    public void setProcessRole(ProcessRoleResource processRole) {
        this.processRole = processRole;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationResource organisation) {
        this.organisation = organisation;
    }

    public boolean hasSameOrganisation(ApplicantResource other) {
        return other.getOrganisation().getId().equals(organisation.getId());
    }

    public boolean isSameUser(ApplicantResource other) {
        return other.getUser().getId().equals(user.getId());
    }

    public boolean isLead() {
        return getProcessRole().getRoleName().equals("leadapplicant");
    }

}