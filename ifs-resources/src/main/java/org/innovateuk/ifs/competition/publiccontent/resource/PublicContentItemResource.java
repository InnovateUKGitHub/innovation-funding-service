package org.innovateuk.ifs.competition.publiccontent.resource;


import java.time.ZonedDateTime;

public class PublicContentItemResource {
    private PublicContentResource publicContentResource;
    private String competitionTitle;
    private ZonedDateTime competitionOpenDate;
    private ZonedDateTime competitionCloseDate;
    private String nonIfsUrl;
    private Boolean isNonIfs;
    private Boolean setupComplete;

    public PublicContentItemResource() {
    }

    public PublicContentItemResource(PublicContentResource publicContentResource, String competitionTitle, ZonedDateTime competitionOpenDate, ZonedDateTime competitionCloseDate) {
        this.publicContentResource = publicContentResource;
        this.competitionTitle = competitionTitle;
        this.competitionOpenDate = competitionOpenDate;
        this.competitionCloseDate = competitionCloseDate;
    }

    public PublicContentResource getPublicContentResource() {
        return publicContentResource;
    }

    public void setPublicContentResource(PublicContentResource publicContentResource) {
        this.publicContentResource = publicContentResource;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
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

    public String getNonIfsUrl() {
        return nonIfsUrl;
    }

    public void setNonIfsUrl(String nonIfsUrl) {
        this.nonIfsUrl = nonIfsUrl;
    }

    public Boolean getNonIfs() {
        return isNonIfs;
    }

    public void setNonIfs(Boolean nonIfs) {
        isNonIfs = nonIfs;
    }

    public Boolean getSetupComplete() {
        return setupComplete;
    }

    public void setSetupComplete(Boolean setupComplete) {
        this.setupComplete = setupComplete;
    }
}
