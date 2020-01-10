package org.innovateuk.ifs.project.partnerdetails.viewmodel;

import java.util.List;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

public class SelectPartnerViewModel {

    private final List<OrganisationResource> partnerOrganisations;

//    private final long projectId;
//    private final String projectName;
//    private final boolean loanCompetition;

    public SelectPartnerViewModel(List<OrganisationResource> partnerOrganisations,
                                   long projectId,
                                   String projectName,
                                   boolean loanCompetition) {
        this.partnerOrganisations = partnerOrganisations;
//        this.projectId = projectId;
//        this.projectName = projectName;
//        this.loanCompetition = loanCompetition;
    }

    public List<OrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
    }

//    public long getProjectId() {
//        return projectId;
//    }
//
//    public String getProjectName() {
//        return projectName;
//    }
//
//    public boolean isLoanCompetition() {
//        return loanCompetition;
//    }
}
