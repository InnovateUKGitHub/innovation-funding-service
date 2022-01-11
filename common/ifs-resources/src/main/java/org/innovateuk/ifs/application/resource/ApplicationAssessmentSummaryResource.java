package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;

/**
 * DTO for the summary of an Application viewed in Assessor Management.
 */
public class ApplicationAssessmentSummaryResource {

    private long id;
    private String name;
    private String innovationArea;
    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private String leadOrganisation;
    private List<String> partnerOrganisations;

    public ApplicationAssessmentSummaryResource() {
    }

    public ApplicationAssessmentSummaryResource(long id, String name, String innovationArea, Long competitionId, String competitionName, CompetitionStatus competitionStatus, String leadOrganisation, List<String> partnerOrganisations) {
        this.id = id;
        this.name = name;
        this.innovationArea = innovationArea;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.leadOrganisation = leadOrganisation;
        this.partnerOrganisations = partnerOrganisations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(String innovationArea) {
        this.innovationArea = innovationArea;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public List<String> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public void setPartnerOrganisations(List<String> partnerOrganisations) {
        this.partnerOrganisations = partnerOrganisations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentSummaryResource that = (ApplicationAssessmentSummaryResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(innovationArea, that.innovationArea)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(competitionStatus, that.competitionStatus)
                .append(leadOrganisation, that.leadOrganisation)
                .append(partnerOrganisations, that.partnerOrganisations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(innovationArea)
                .append(competitionId)
                .append(competitionName)
                .append(competitionStatus)
                .append(leadOrganisation)
                .append(partnerOrganisations)
                .toHashCode();
    }
}