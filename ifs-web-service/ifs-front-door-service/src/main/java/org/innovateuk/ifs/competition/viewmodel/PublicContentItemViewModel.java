package org.innovateuk.ifs.competition.viewmodel;

import org.innovateuk.ifs.competition.status.PublicContentStatusText;

import java.time.ZonedDateTime;

/**
 * View model for Competition Public Content Search items.
 */
public class PublicContentItemViewModel {

    private String shortDescription;
    private String eligibilitySummary;
    private String competitionTitle;
    private Long competitionId;
    private ZonedDateTime competitionOpenDate;
    private ZonedDateTime competitionCloseDate;
    private ZonedDateTime registrationCloseDate;
    private PublicContentStatusText publicContentStatusText;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getEligibilitySummary() {
        return eligibilitySummary;
    }

    public void setEligibilitySummary(String eligibilitySummary) {
        this.eligibilitySummary = eligibilitySummary;
    }

    public ZonedDateTime getCompetitionOpenDate() {
        return competitionOpenDate;
    }

    public void setCompetitionOpenDate(ZonedDateTime competitionOpenDate) {
        this.competitionOpenDate = competitionOpenDate;
    }

    public ZonedDateTime getCompetitionCloseDate() {
        return competitionCloseDate;
    }

    public void setCompetitionCloseDate(ZonedDateTime competitionCloseDate) {
        this.competitionCloseDate = competitionCloseDate;
    }

    public ZonedDateTime getRegistrationCloseDate() {
        return registrationCloseDate;
    }

    public void setRegistrationCloseDate(ZonedDateTime registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
    }

    public PublicContentStatusText getPublicContentStatusText() {
        return publicContentStatusText;
    }

    public void setPublicContentStatusText(PublicContentStatusText publicContentStatusText) {
        this.publicContentStatusText = publicContentStatusText;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
