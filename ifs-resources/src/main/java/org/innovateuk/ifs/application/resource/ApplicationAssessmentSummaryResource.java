package org.innovateuk.ifs.application.resource;

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

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (competitionId != null ? !competitionId.equals(that.competitionId) : that.competitionId != null) {
            return false;
        }
        if (competitionName != null ? !competitionName.equals(that.competitionName) : that.competitionName != null) {
            return false;
        }
        return partnerOrganisations != null ? partnerOrganisations.equals(that.partnerOrganisations) : that.partnerOrganisations == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (competitionId != null ? competitionId.hashCode() : 0);
        result = 31 * result + (competitionName != null ? competitionName.hashCode() : 0);
        result = 31 * result + (partnerOrganisations != null ? partnerOrganisations.hashCode() : 0);
        return result;
    }
}