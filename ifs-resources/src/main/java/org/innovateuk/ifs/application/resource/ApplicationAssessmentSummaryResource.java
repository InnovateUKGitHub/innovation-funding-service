package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * DTO for the summary of an Application viewed in Assessor Management.
 */
public class ApplicationAssessmentSummaryResource {

    private Long id;
    private String name;
    private Long competitionId;
    private String competitionName;
    private List<String> partnerOrganisations;

    public ApplicationAssessmentSummaryResource() {
    }

    public ApplicationAssessmentSummaryResource(Long id, String name, Long competitionId, String competitionName, List<String> partnerOrganisations) {
        this.id = id;
        this.name = name;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.partnerOrganisations = partnerOrganisations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public void setPartnerOrganisations(List<String> partnerOrganisations) {
        this.partnerOrganisations = partnerOrganisations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationAssessmentSummaryResource that = (ApplicationAssessmentSummaryResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(partnerOrganisations, that.partnerOrganisations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(competitionId)
                .append(competitionName)
                .append(partnerOrganisations)
                .toHashCode();
    }
}