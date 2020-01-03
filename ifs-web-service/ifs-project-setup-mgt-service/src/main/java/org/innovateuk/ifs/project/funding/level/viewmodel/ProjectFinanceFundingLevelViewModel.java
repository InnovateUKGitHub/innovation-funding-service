package org.innovateuk.ifs.project.funding.level.viewmodel;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectFinanceFundingLevelViewModel {

    private final long projectId;
    private final long applicationId;
    private final String projectName;
    private final List<ProjectFinancePartnerFundingSoughtViewModel> partners;

    public ProjectFinanceFundingLevelViewModel(ProjectResource project, List<ProjectFinanceResource> finances) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.partners =  finances.stream()
                .map(pf -> new ProjectFinancePartnerFundingSoughtViewModel(pf.getOrganisation(), pf.getOrganisationName(), pf.getTotalFundingSought()))
                .collect(Collectors.toList());
    }

    public long getProjectId() {
        return projectId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<ProjectFinancePartnerFundingSoughtViewModel> getPartners() {
        return partners;
    }
}
