package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

/**
 * ViewModel of a CompetitionInvite.
 */
public class CompetitionInviteViewModel {

    private String competitionInviteHash;
    private String competitionName;
    private LocalDateTime acceptsDate;
    private LocalDateTime deadlineDate;

    public CompetitionInviteViewModel(String competitionInviteHash, String competitionName, LocalDateTime acceptsDate, LocalDateTime deadlineDate) {
        this.competitionInviteHash = competitionInviteHash;
        this.competitionName = competitionName;
        this.acceptsDate = acceptsDate;
        this.deadlineDate = deadlineDate;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public LocalDateTime getAcceptsDate() {
        return acceptsDate;
    }

    public void setAcceptsDate(LocalDateTime acceptsDate) {
        this.acceptsDate = acceptsDate;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompetitionInviteViewModel that = (CompetitionInviteViewModel) o;

        return new EqualsBuilder()
                .append(competitionInviteHash, that.competitionInviteHash)
                .append(competitionName, that.competitionName)
                .append(acceptsDate, that.acceptsDate)
                .append(deadlineDate, that.deadlineDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionInviteHash)
                .append(competitionName)
                .append(acceptsDate)
                .append(deadlineDate)
                .toHashCode();
    }
}
