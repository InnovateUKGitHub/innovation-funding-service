package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;

/**
 * Holder of model attributes for the Application Progress view.
 */
public class ApplicationAssessmentProgressViewModel {

    private long applicationId;
    private String applicationName;
    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private String leadOrganisation;
    private List<String> partnerOrganisations;
    private List<ApplicationAssessmentProgressAssignedRowViewModel> assigned;
    private List<ApplicationAvailableAssessorsRowViewModel> available;
    private List<ApplicationAssessmentProgressRejectedRowViewModel> rejected;
    private List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> previouslyAssigned;

    public ApplicationAssessmentProgressViewModel(long applicationId,
                                                  String applicationName,
                                                  Long competitionId,
                                                  String competitionName,
                                                  CompetitionStatus competitionStatus,
                                                  String leadOrganisation,
                                                  List<String> partnerOrganisations,
                                                  List<ApplicationAssessmentProgressAssignedRowViewModel> assigned,
                                                  List<ApplicationAssessmentProgressRejectedRowViewModel> rejected,
                                                  List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> previouslyAssigned,
                                                  List<ApplicationAvailableAssessorsRowViewModel> available) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.leadOrganisation = leadOrganisation;
        this.partnerOrganisations = partnerOrganisations;
        this.assigned = assigned;
        this.rejected = rejected;
        this.previouslyAssigned = previouslyAssigned;
        this.available = available;
    }

    public long getApplicationId() {
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

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public List<String> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public List<ApplicationAssessmentProgressAssignedRowViewModel> getAssigned() {
        return assigned;
    }

    public List<ApplicationAssessmentProgressRejectedRowViewModel> getRejected() {
        return rejected;
    }

    public List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> getPreviouslyAssigned() {
        return previouslyAssigned;
    }

    public List<ApplicationAvailableAssessorsRowViewModel> getAvailable() {
        return available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentProgressViewModel that = (ApplicationAssessmentProgressViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(competitionStatus, that.competitionStatus)
                .append(leadOrganisation, that.leadOrganisation)
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(assigned, that.assigned)
                .append(available, that.available)
                .append(rejected, that.rejected)
                .append(previouslyAssigned, that.previouslyAssigned)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(competitionId)
                .append(competitionName)
                .append(competitionStatus)
                .append(leadOrganisation)
                .append(partnerOrganisations)
                .append(assigned)
                .append(available)
                .append(rejected)
                .append(previouslyAssigned)
                .toHashCode();
    }
}