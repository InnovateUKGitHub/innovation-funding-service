package org.innovateuk.ifs.publiccontent.domain;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Entity to represent the competitions content that is visible to the public.
 */
@Entity
public class PublicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long competitionId;

    private ZonedDateTime publishDate;

    private String shortDescription;

    private String projectFundingRange;

    private String eligibilitySummary;

    private String projectSize;

    @Column(length=5000)
    private String summary;

    @Enumerated(EnumType.STRING)
    private FundingType fundingType;

    @OneToMany(mappedBy="publicContent")
    private List<ContentSection> contentSections;

    @OneToMany(mappedBy="publicContent")
    private List<Keyword> keywords;

    @OneToMany(mappedBy="publicContent")
    private List<ContentEvent> contentEvents;

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

    public List<ContentSection> getContentSections() {
        return contentSections;
    }

    public void setContentSections(List<ContentSection> contentSections) {
        this.contentSections = contentSections;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<ContentEvent> getContentEvents() {
        return contentEvents;
    }

    public void setContentEvents(List<ContentEvent> contentEvents) {
        this.contentEvents = contentEvents;
    }
}
