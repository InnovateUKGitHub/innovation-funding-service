package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

public class InterviewResource {
    private Long id;
    private String event;
    private InterviewState interviewState;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long processRole;
    private Long internalParticipant;
    private Long application;
    private String applicationName;
    private Long competition;

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

    public InterviewState getInterviewState() {
        return interviewState;
    }

    public void setInterviewState(InterviewState interviewState) {
        this.interviewState = interviewState;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewResource that = (InterviewResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(event, that.event)
                .append(interviewState, that.interviewState)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
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
                .append(interviewState)
                .append(startDate)
                .append(endDate)
                .append(processRole)
                .append(internalParticipant)
                .append(application)
                .append(applicationName)
                .append(competition)
                .toHashCode();
    }
}
