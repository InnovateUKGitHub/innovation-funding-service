package org.innovateuk.ifs.competition.publiccontent.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * The resource for competition public content.
 */
public class PublicContentResource {

    private Long id;

    private Long competitionId;

    private ZonedDateTime publishDate;

    private String shortDescription;

    private String projectFundingRange;

    private String eligibilitySummary;

    private String projectSize;

    private String summary;

    private FundingType fundingType;

    private List<PublicContentSectionResource> contentSections;

    private List<ContentEventResource> contentEvents;

    private List<String> keywords;

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

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
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

    public List<ContentEventResource> getContentEvents() {
        return contentEvents;
    }

    public void setContentEvents(List<ContentEventResource> contentEvents) {
        this.contentEvents = contentEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PublicContentResource resource = (PublicContentResource) o;

        return new EqualsBuilder()
                .append(id, resource.id)
                .append(competitionId, resource.competitionId)
                .append(publishDate, resource.publishDate)
                .append(shortDescription, resource.shortDescription)
                .append(projectFundingRange, resource.projectFundingRange)
                .append(eligibilitySummary, resource.eligibilitySummary)
                .append(projectSize, resource.projectSize)
                .append(summary, resource.summary)
                .append(fundingType, resource.fundingType)
                .append(contentSections, resource.contentSections)
                .append(contentEvents, resource.contentEvents)
                .append(keywords, resource.keywords)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competitionId)
                .append(publishDate)
                .append(shortDescription)
                .append(projectFundingRange)
                .append(eligibilitySummary)
                .append(projectSize)
                .append(summary)
                .append(fundingType)
                .append(contentSections)
                .append(keywords)
                .toHashCode();
    }
}
