package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

public class AssessmentResource {
    private Long id;
    private String event;
    private AssessmentState assessmentState;
    private LocalDate startDate;
    private LocalDate endDate;
    private AssessmentFundingDecisionOutcomeResource fundingDecision;
    private AssessmentRejectOutcomeResource rejection;
    private Long processRole;
    private Long internalParticipant;
    private Long application;
    private String applicationName;
    private Long competition;
    private boolean collaborativeProject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public AssessmentState getAssessmentState() {
        return assessmentState;
    }

    public void setAssessmentState(AssessmentState assessmentState) {
        this.assessmentState = assessmentState;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public AssessmentFundingDecisionOutcomeResource getFundingDecision() {
        return fundingDecision;
    }

    public void setFundingDecision(AssessmentFundingDecisionOutcomeResource fundingDecision) {
        this.fundingDecision = fundingDecision;
    }

    public AssessmentRejectOutcomeResource getRejection() {
        return rejection;
    }

    public void setRejection(AssessmentRejectOutcomeResource rejection) {
        this.rejection = rejection;
    }

    public Long getProcessRole() {
        return processRole;
    }

    public void setProcessRole(Long processRole) {
        this.processRole = processRole;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public Long getInternalParticipant() {
        return internalParticipant;
    }

    public void setInternalParticipant(Long internalParticipant) {
        this.internalParticipant = internalParticipant;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public void setCollaborativeProject(final boolean collaborativeProject) {
        this.collaborativeProject = collaborativeProject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentResource that = (AssessmentResource) o;

        return new EqualsBuilder()
                .append(collaborativeProject, that.collaborativeProject)
                .append(id, that.id)
                .append(event, that.event)
                .append(assessmentState, that.assessmentState)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
                .append(fundingDecision, that.fundingDecision)
                .append(rejection, that.rejection)
                .append(processRole, that.processRole)
                .append(internalParticipant, that.internalParticipant)
                .append(application, that.application)
                .append(applicationName, that.applicationName)
                .append(competition, that.competition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(event)
                .append(assessmentState)
                .append(startDate)
                .append(endDate)
                .append(fundingDecision)
                .append(rejection)
                .append(processRole)
                .append(internalParticipant)
                .append(application)
                .append(applicationName)
                .append(competition)
                .append(collaborativeProject)
                .toHashCode();
    }
}
