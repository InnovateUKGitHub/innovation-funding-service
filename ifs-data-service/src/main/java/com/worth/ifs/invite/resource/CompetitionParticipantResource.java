package com.worth.ifs.invite.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.resource.CompetitionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * DTO for {@link com.worth.ifs.invite.domain.CompetitionParticipant}s.
 */
public class CompetitionParticipantResource {

    private Long id;
    private Long competitionId;
    private Long userId;
    private CompetitionInviteResource invite;
    private RejectionReasonResource rejectionReason;
    private String rejectionReasonComment;
    private CompetitionParticipantRoleResource role;
    private ParticipantStatusResource status;
    private String competitionName;
    private LocalDateTime assessmentStartDate;
    private LocalDateTime assessmentEndDate;
//    private CompetitionStatus competitionStatus;

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CompetitionInviteResource getInvite() {
        return invite;
    }

    public void setInvite(CompetitionInviteResource invite) {
        this.invite = invite;
    }

    public RejectionReasonResource getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(RejectionReasonResource rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReasonComment() {
        return rejectionReasonComment;
    }

    public void setRejectionReasonComment(String rejectionReasonComment) {
        this.rejectionReasonComment = rejectionReasonComment;
    }

    public CompetitionParticipantRoleResource getRole() {
        return role;
    }

    public void setRole(CompetitionParticipantRoleResource role) {
        this.role = role;
    }

    public ParticipantStatusResource getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatusResource status) {
        this.status = status;
    }

    public LocalDateTime getAssessmentStartDate() {
        return assessmentStartDate;
    }

    public void setAssessmentStartDate(LocalDateTime assessmentStartDate) {
        this.assessmentStartDate = assessmentStartDate;
    }

    public LocalDateTime getAssessmentEndDate() {
        return assessmentEndDate;
    }

    public void setAssessmentEndDate(LocalDateTime assessmentEndDate) {
        this.assessmentEndDate = assessmentEndDate;
    }


//    public CompetitionStatus getCompetitionStatus() {
//        return competitionStatus;
//    }
//
//    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
//        this.competitionStatus = competitionStatus;
//    }


    @JsonIgnore
    public long getAssessmentDaysLeft() {
        return DAYS.between(LocalDateTime.now(), assessmentEndDate);
    }

    @JsonIgnore
    public long getAssessmentDaysLeftPercentage() {
        return getDaysLeftPercentage(getAssessmentDaysLeft(), DAYS.between(assessmentStartDate, assessmentEndDate));
    }

    @JsonIgnore
    public boolean isInAssessment() {
        return assessmentStartDate.isBefore(LocalDateTime.now()) && assessmentEndDate.isAfter(LocalDateTime.now());
    }

//    @JsonIgnore
//    public boolean isInAssessment() {
//        return this.competitionStatus == CompetitionStatus.IN_ASSESSMENT;
//    }

    @JsonIgnore
    public boolean isAnUpcomingAssessment() {
        return assessmentStartDate.isAfter(LocalDateTime.now());
    }


    private static long getDaysLeftPercentage(long daysLeft, long totalDays) {
        if (daysLeft <= 0) {
            return 100;
        }
        double deadlineProgress = 100 - (((double) daysLeft / (double) totalDays) * 100);
        long startDateToEndDatePercentage = (long) deadlineProgress;
        return startDateToEndDatePercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionParticipantResource that = (CompetitionParticipantResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(competitionId, that.competitionId)
                .append(userId, that.userId)
                .append(invite, that.invite)
                .append(rejectionReason, that.rejectionReason)
                .append(rejectionReasonComment, that.rejectionReasonComment)
                .append(role, that.role)
                .append(status, that.status)
                .append(competitionName, that.competitionName)
                .append(assessmentStartDate, that.assessmentStartDate)
                .append(assessmentEndDate, that.assessmentEndDate)
//                .append(competitionStatus, that.competitionStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competitionId)
                .append(userId)
                .append(invite)
                .append(rejectionReason)
                .append(rejectionReasonComment)
                .append(role)
                .append(status)
                .append(competitionName)
                .append(assessmentStartDate)
                .append(assessmentEndDate)
//                .append(competitionStatus)
                .toHashCode();
    }
}
