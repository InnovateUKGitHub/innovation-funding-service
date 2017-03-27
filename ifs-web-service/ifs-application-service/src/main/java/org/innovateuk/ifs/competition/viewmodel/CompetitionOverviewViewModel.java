package org.innovateuk.ifs.competition.viewmodel;

import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for the competition overview with the public content data
 */
public class CompetitionOverviewViewModel {
    private String competitionTitle;
    private LocalDateTime competitionOpenDate;
    private LocalDateTime registrationCloseDate;
    private LocalDateTime competitionCloseDate;
    private Long competitionId;
    private String shortDescription;
    private String nonIfsUrl;
    private Boolean nonIfs;
    private List<AbstractPublicSectionContentViewModel> allSections;
    private boolean userIsLoggedIn = false;

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public LocalDateTime getCompetitionOpenDate() {
        return competitionOpenDate;
    }

    public void setCompetitionOpenDate(LocalDateTime competitionOpenDate) {
        this.competitionOpenDate = competitionOpenDate;
    }

    public LocalDateTime getRegistrationCloseDate() {
        return registrationCloseDate;
    }

    public void setRegistrationCloseDate(LocalDateTime registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
    }

    public LocalDateTime getCompetitionCloseDate() {
        return competitionCloseDate;
    }

    public void setCompetitionCloseDate(LocalDateTime competitionCloseDate) {
        this.competitionCloseDate = competitionCloseDate;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<AbstractPublicSectionContentViewModel> getAllSections() {
        return allSections;
    }

    public void setAllSections(List<AbstractPublicSectionContentViewModel> allSections) {
        this.allSections = allSections;
    }

    public String getNonIfsUrl() {
        return nonIfsUrl;
    }

    public void setNonIfsUrl(String nonIfsUrl) {
        this.nonIfsUrl = nonIfsUrl;
    }

    public Boolean getNonIfs() {
        return nonIfs;
    }

    public void setNonIfs(Boolean nonIfs) {
        this.nonIfs = nonIfs;
    }

    public boolean isUserIsLoggedIn() {
        return userIsLoggedIn;
    }

    public void setUserIsLoggedIn(boolean userIsLoggedIn) {
        this.userIsLoggedIn = userIsLoggedIn;
    }

    public boolean isShowNotOpenYetMessage() {
        return getCompetitionOpenDate().isAfter(LocalDateTime.now());
    }

    public boolean isShowClosedMessage() {
        return nonIfs ?  getRegistrationCloseDate().isBefore(LocalDateTime.now()) :
                getCompetitionCloseDate().isBefore(LocalDateTime.now());
    }

    public boolean isDisableApplyButton() {
        return isShowNotOpenYetMessage() || isShowClosedMessage();
    }

    public String getApplyButtonUrl() {
        if (nonIfs) {
            return nonIfsUrl;
        } else if (userIsLoggedIn) {
            return "/application/create-authenticated/" + competitionId;
        } else {
            return "/application/create/check-eligibility/" + competitionId;
        }
    }

    public String getApplyButtonText() {
        return nonIfs ? "Register and apply online" : "Start new application";
    }

    public boolean isShowSignInText() {
        return !nonIfs && !isDisableApplyButton();
    }
}
