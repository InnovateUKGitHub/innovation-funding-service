package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private List<ApplicationAssessmentProgressAssignedRowViewModel> assigned;
    private List<ApplicationAvailableAssessorsRowViewModel> available;
    private List<ApplicationAssessmentProgressRejectedRowViewModel> rejected;
    private List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> previouslyAssigned;

    public ApplicationAssessmentProgressViewModel(Long applicationId,
                                                  String applicationName,
                                                  Long competitionId,
                                                  String competitionName,
                                                  List<String> partnerOrganisations,
                                                  List<ApplicationAssessmentProgressAssignedRowViewModel> assigned,
                                                  List<ApplicationAssessmentProgressRejectedRowViewModel> rejected,
                                                  List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> previouslyAssigned,
                                                  List<ApplicationAvailableAssessorsRowViewModel> available) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.partnerOrganisations = partnerOrganisations;
        this.assigned = assigned;
        this.rejected = rejected;
        this.previouslyAssigned = previouslyAssigned;
        this.available = available;
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
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(assigned, that.assigned)
                .append(rejected, that.rejected)
                .append(previouslyAssigned, that.previouslyAssigned)
                .append(available, that.available)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(competitionId)
                .append(competitionName)
                .append(partnerOrganisations)
                .append(assigned)
                .append(rejected)
                .append(previouslyAssigned)
                .append(available)
                .toHashCode();
    }
}