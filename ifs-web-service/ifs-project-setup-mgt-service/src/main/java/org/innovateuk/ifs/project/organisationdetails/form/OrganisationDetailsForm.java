package org.innovateuk.ifs.project.organisationdetails.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

public class OrganisationDetailsForm {

    private long projectId;

    private OrganisationResource partnerOrganisation;

    private YourOrganisationWithGrowthTableForm organisationSizeForm;

    public long getProjectId() {
        return projectId;
    }

    public OrganisationResource getPartnerOrganisation() {
        return partnerOrganisation;
    }

    public void setOrganisationSizeForm(YourOrganisationWithGrowthTableForm organisationSizeForm) {
        this.organisationSizeForm = organisationSizeForm;
    }
}
