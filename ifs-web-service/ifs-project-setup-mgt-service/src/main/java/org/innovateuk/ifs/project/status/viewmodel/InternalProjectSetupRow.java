package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.util.Map;

public class InternalProjectSetupRow {

    private String projectName;
    private Long applicationNumber;
    private ProjectState projectState;
    private int numberOfPartners;
    private long competitionId;
    private String projectLeadOrganisationName;
    private Long projectId;

    private Map<String, ProjectActivityStates> states;

    public InternalProjectSetupRow(String projectName, Long applicationNumber, ProjectState projectState, int numberOfPartners, long competitionId, String projectLeadOrganisationName, Long projectId, Map<String, ProjectActivityStates> states) {
        this.projectName = projectName;
        this.applicationNumber = applicationNumber;
        this.projectState = projectState;
        this.numberOfPartners = numberOfPartners;
        this.competitionId = competitionId;
        this.projectLeadOrganisationName = projectLeadOrganisationName;
        this.projectId = projectId;
        this.states = states;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationNumber() {
        return applicationNumber;
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public int getNumberOfPartners() {
        return numberOfPartners;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getProjectLeadOrganisationName() {
        return projectLeadOrganisationName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Map<String, ProjectActivityStates> getStates() {
        return states;
    }
}
