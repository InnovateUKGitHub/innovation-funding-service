package com.worth.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class AssessmentResource {
    private Long id;
    private String event;
    private AssessmentStates assessmentState;
    private Calendar lastModified;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> processOutcomes;
    private Long processRole;
    private Long internalParticipant;
    private Boolean submitted;
    private Boolean started;
    private Long application;
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

    public AssessmentStates getAssessmentState() {
        return assessmentState;
    }

    public void setAssessmentState(AssessmentStates assessmentState) {
        this.assessmentState = assessmentState;
    }

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
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

    public List<Long> getProcessOutcomes() {
        return processOutcomes;
    }

    public void setProcessOutcomes(List<Long> processOutcomes) {
        this.processOutcomes = processOutcomes;
    }

    public Long getProcessRole() {
        return processRole;
    }

    public void setProcessRole(Long processRole) {
        this.processRole = processRole;
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
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
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentResource that = (AssessmentResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(event, that.event)
                .append(assessmentState, that.assessmentState)
                .append(lastModified, that.lastModified)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
                .append(processOutcomes, that.processOutcomes)
                .append(processRole, that.processRole)
                .append(submitted, that.submitted)
                .append(started, that.started)
                .append(application, that.application)
                .append(competition, that.competition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(event)
                .append(assessmentState)
                .append(lastModified)
                .append(startDate)
                .append(endDate)
                .append(processOutcomes)
                .append(processRole)
                .append(submitted)
                .append(started)
                .append(application)
                .append(competition)
                .toHashCode();
    }
}
