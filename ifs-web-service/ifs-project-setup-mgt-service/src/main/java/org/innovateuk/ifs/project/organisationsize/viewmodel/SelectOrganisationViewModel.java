package org.innovateuk.ifs.project.organisationsize.viewmodel;

import java.util.List;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

public class SelectOrganisationViewModel {

    private final List<PartnerOrganisationResource> partnerOrganisations;
    private final long projectId;
    private final String projectName;
    private final long competitionId;

    public SelectOrganisationViewModel(long projectId, String projectName, long competitionId,
                                       List<PartnerOrganisationResource> partnerOrganisations) {
        this.partnerOrganisations = partnerOrganisations;
        this.projectId = projectId;
        this.competitionId = competitionId;
        this.projectName = projectName;
    }

    public long getCompetitionId() {
        return competitionId;
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