package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Application Progress view.
 */
public class ApplicationAssessmentProgressViewModel {

    private Long applicationId;
    private String applicationName;
    private Long competitionId;
    private String competitionName;
    private List<String> partnerOrganisations;

    public ApplicationAssessmentProgressViewModel(Long applicationId, String applicationName, Long competitionId, String competitionName, List<String> partnerOrganisations) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.partnerOrganisations = partnerOrganisations;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<String> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationAssessmentProgressViewModel that = (ApplicationAssessmentProgressViewModel) o;

        if (applicationId != null ? !applicationId.equals(that.applicationId) : that.applicationId != null) {
            return false;
        }
        if (applicationName != null ? !applicationName.equals(that.applicationName) : that.applicationName != null) {
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
        int result = applicationId != null ? applicationId.hashCode() : 0;
        result = 31 * result + (applicationName != null ? applicationName.hashCode() : 0);
        result = 31 * result + (competitionId != null ? competitionId.hashCode() : 0);
        result = 31 * result + (competitionName != null ? competitionName.hashCode() : 0);
        result = 31 * result + (partnerOrganisations != null ? partnerOrganisations.hashCode() : 0);
        return result;
    }
}