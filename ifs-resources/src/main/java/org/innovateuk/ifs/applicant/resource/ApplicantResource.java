package org.innovateuk.ifs.applicant.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;

/**
 * Rich resource for an applicant in an application.
 */
public class ApplicantResource {

    private ProcessRoleResource processRole;

    private OrganisationResource organisation;

    public ProcessRoleResource getProcessRole() {
        return processRole;
    }

    public void setProcessRole(ProcessRoleResource processRole) {
        this.processRole = processRole;
    }

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationResource organisation) {
        this.organisation = organisation;
    }

    public String getName() {
        return processRole.getUserName();
    }

    public boolean hasSameOrganisation(ApplicantResource other) {
        return other.getOrganisation().getId().equals(organisation.getId());
    }

    @JsonIgnore
    public boolean isSameUser(ApplicantResource other) {
        return other.getProcessRole().getId().equals(processRole.getId());
    }

    @JsonIgnore
    public boolean isLead() {
        return LEADAPPLICANT.equals(getProcessRole().getRole());
    }

    @JsonIgnore
    public boolean isResearch() { return getOrganisation().getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId()); }

}