package org.innovateuk.ifs.competition.publiccontent.resource;


import java.time.LocalDateTime;
import java.util.List;

public class PublicContentResource {

    private Long id;

    private Long competitionId;

    private LocalDateTime publishDate;

    private String shortDescription;

    private String projectFundingRange;

    private String eligibilitySummary;

    private String projectSize;

    private String summary;

    private FundingType fundingType;

    private List<PublicContentSectionResource> contentSections;

    private List<String> keywords;

    //TODO events.


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

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getProjectFundingRange() {
        return projectFundingRange;
    }

    public void setProjectFundingRange(String projectFundingRange) {
        this.projectFundingRange = projectFundingRange;
    }

    public String getEligibilitySummary() {
        return eligibilitySummary;
    }

    public void setEligibilitySummary(String eligibilitySummary) {
        this.eligibilitySummary = eligibilitySummary;
    }

    public String getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(String projectSize) {
        this.projectSize = projectSize;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public List<PublicContentSectionResource> getContentSections() {
        return contentSections;
    }

    public void setContentSections(List<PublicContentSectionResource> contentSections) {
        this.contentSections = contentSections;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
