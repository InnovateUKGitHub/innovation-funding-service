package org.innovateuk.ifs.project.grants.viewmodel;

import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;

import java.util.List;

public class ManageInvitationsViewModel {

    private long competitionId;
    private String competitionName;
    private Long projectId;
    private String projectName;
    private long applicationId;
    private int totalGrants;
    private List<SentGrantsInviteResource> grants;

    public ManageInvitationsViewModel(long competitionId, String competitionName, Long projectId, String projectName, long applicationId,
                                      List<SentGrantsInviteResource> grants) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.totalGrants = grants.size();
        this.grants = grants;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getTotalGrants() {
        return totalGrants;
    }

    public List<SentGrantsInviteResource> getGrants() {
        return grants;
    }
}
