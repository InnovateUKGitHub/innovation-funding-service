package org.innovateuk.ifs.project.organisationdetails.viewmodel;

import java.util.List;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

public class SelectOrganisationViewModel {

    private final List<PartnerOrganisationResource> partnerOrganisations;
    private final long projectId;
    private final String projectName;

    public SelectOrganisationViewModel(List<PartnerOrganisationResource> partnerOrganisations,
                                       long projectId, String projectName) {
        this.partnerOrganisations = partnerOrganisations;
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public List<PartnerOrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }
}
