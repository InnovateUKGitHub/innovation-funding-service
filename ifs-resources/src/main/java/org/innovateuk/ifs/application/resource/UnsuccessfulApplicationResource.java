package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UnsuccessfulApplicationResource {

    private long id;
    private String name;
    private String leadOrganisationName;
    private ApplicationState applicationState;
    private long competition;

    public UnsuccessfulApplicationResource() {
    }

    public UnsuccessfulApplicationResource(long id, String name, String leadOrganisationName, ApplicationState applicationState, long competition) {
        this.id = id;
        this.name = name;
        this.leadOrganisationName = leadOrganisationName;
        this.applicationState = applicationState;
        this.competition = competition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public void setLeadOrganisationName(String leadOrganisationName) {
        this.leadOrganisationName = leadOrganisationName;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public long getCompetition() {
        return competition;
    }

    public void setCompetition(long competition) {
        this.competition = competition;
    }

    @JsonIgnore
    public String getApplicationStateDisplayName(){
        return applicationState.getDisplayName();
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    @JsonIgnore
    public boolean isOpen(){
        return applicationState == ApplicationState.OPEN || applicationState == ApplicationState.CREATED;
    }

    @JsonIgnore
    public boolean isApproved(){
        return applicationState == ApplicationState.APPROVED;
    }

    @JsonIgnore
    public boolean isSubmitted() {
        return ApplicationState.submittedAndFinishedStates.contains(applicationState);
    }

    @JsonIgnore
    public boolean isWithdrawn() { return ApplicationState.WITHDRAWN.equals(applicationState); }
}
