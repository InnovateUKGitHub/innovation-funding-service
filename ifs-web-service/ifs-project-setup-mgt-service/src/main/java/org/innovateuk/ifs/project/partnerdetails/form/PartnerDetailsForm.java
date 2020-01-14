package org.innovateuk.ifs.project.partnerdetails.form;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;

public class PartnerDetailsForm {

    private long projectId;

    private OrganisationResource partnerOrganisation;

    public long getProjectId() {
        return projectId;
    }

    public OrganisationResource getPartnerOrganisation() {
        return partnerOrganisation;
    }
}
