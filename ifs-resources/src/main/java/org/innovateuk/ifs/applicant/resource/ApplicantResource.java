package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

/**
 * Created by luke.harper on 25/04/2017.
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

    public boolean isSameUser(ApplicantResource other) {
        return other.getProcessRole().getId().equals(processRole.getId());
    }

    public boolean isLead() {
        return getProcessRole().getRoleName().equals("leadapplicant");
    }

    public boolean isResearch() { return getOrganisation().getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId()); }

}