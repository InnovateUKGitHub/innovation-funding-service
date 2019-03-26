package org.innovateuk.ifs.competition.publiccontent.resource;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.competition.resource.CompetitionResource.H2020_TYPE_NAME;

public class PublicContentItemResource {
    private PublicContentResource publicContentResource;
    private String competitionTitle;
    private ZonedDateTime competitionOpenDate;
    private ZonedDateTime competitionCloseDate;
    private ZonedDateTime registrationCloseDate;
    private String nonIfsUrl;
    private Boolean isNonIfs;
    private Boolean setupComplete;
    private FundingType fundingType;
    private String competitionType;

    public PublicContentItemResource() {
    }

    public PublicContentItemResource(PublicContentResource publicContentResource, String competitionTitle, ZonedDateTime competitionOpenDate, ZonedDateTime competitionCloseDate, ZonedDateTime registrationCloseDate, String competitionType) {
        this.publicContentResource = publicContentResource;
        this.competitionTitle = competitionTitle;
        this.competitionOpenDate = competitionOpenDate;
        this.competitionCloseDate = competitionCloseDate;
        this.registrationCloseDate = registrationCloseDate;
        this.competitionType = competitionType;
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

    public ZonedDateTime getRegistrationCloseDate() {
        return registrationCloseDate;
    }

    public void setRegistrationCloseDate(ZonedDateTime registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
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

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(String competitionType) {
        this.competitionType = competitionType;
    }

    @JsonIgnore
    public boolean isH2020() {
        return ofNullable(competitionType)
                .map(name -> name.equals(H2020_TYPE_NAME))
                .orElse(false);
    }
}
