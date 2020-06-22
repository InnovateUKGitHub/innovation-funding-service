package org.innovateuk.ifs.project.grants.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;
import java.util.stream.Collectors;

public class GrantsInviteSendViewModel {
    private long applicationId;
    private long projectId;
    private String projectName;
    private List<Pair<Long, String>> organisationNameIdPairs;

    public GrantsInviteSendViewModel(ProjectResource project, List<PartnerOrganisationResource> partners) {
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.organisationNameIdPairs = partners.stream().map(org -> Pair.of(org.getOrganisation(), org.getOrganisationName())).collect(Collectors.toList());
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<Pair<Long, String>> getOrganisationNameIdPairs() {
        return organisationNameIdPairs;
    }
}
