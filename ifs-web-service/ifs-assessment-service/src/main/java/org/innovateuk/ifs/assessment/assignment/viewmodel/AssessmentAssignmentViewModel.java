package org.innovateuk.ifs.assessment.assignment.viewmodel;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.SortedSet;

/**
 * Holder of model attributes for the acceptance of an application by an Assessor
 */
@Getter
public class AssessmentAssignmentViewModel {

    private Long assessmentId;
    private Long competitionId;
    private String applicationName;
    private final boolean alwaysOpen;
    private final Long assessmentPeriodId;
    private SortedSet<OrganisationResource> partners;
    private OrganisationResource leadPartner;
    private String projectSummary;

    public AssessmentAssignmentViewModel(Long assessmentId,
                                         Long competitionId,
                                         String applicationName,
                                         boolean alwaysOpen,
                                         Long assessmentPeriodId,
                                         SortedSet<OrganisationResource> partners,
                                         OrganisationResource leadPartner,
                                         String projectSummary) {
        this.assessmentId = assessmentId;
        this.competitionId = competitionId;
        this.applicationName = applicationName;
        this.alwaysOpen = alwaysOpen;
        this.assessmentPeriodId = assessmentPeriodId;
        this.partners = partners;
        this.leadPartner = leadPartner;
        this.projectSummary = projectSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentAssignmentViewModel that = (AssessmentAssignmentViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(competitionId, that.competitionId)
                .append(applicationName, that.applicationName)
                .append(alwaysOpen, that.alwaysOpen)
                .append(assessmentPeriodId, that.assessmentPeriodId)
                .append(partners, that.partners)
                .append(leadPartner, that.leadPartner)
                .append(projectSummary, that.projectSummary)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(competitionId)
                .append(applicationName)
                .append(alwaysOpen)
                .append(assessmentPeriodId)
                .append(partners)
                .append(leadPartner)
                .append(projectSummary)
                .toHashCode();
    }
}
