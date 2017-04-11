package org.innovateuk.ifs.assessment.assignment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.SortedSet;

/**
 * Holder of model attributes for the acceptance of an application by an Assessor
 */
public class AssessmentAssignmentViewModel {

    private Long assessmentId;
    private Long competitionId;
    private String applicationName;
    private SortedSet<OrganisationResource> partners;
    private OrganisationResource leadPartner;
    private String projectSummary;

    public AssessmentAssignmentViewModel(Long assessmentId,
                                         Long competitionId,
                                         String applicationName,
                                         SortedSet<OrganisationResource> partners,
                                         OrganisationResource leadPartner,
                                         String projectSummary) {
        this.assessmentId = assessmentId;
        this.competitionId = competitionId;
        this.applicationName = applicationName;
        this.partners = partners;
        this.leadPartner = leadPartner;
        this.projectSummary = projectSummary;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public SortedSet<OrganisationResource> getPartners() {
        return partners;
    }

    public OrganisationResource getLeadPartner() {
        return leadPartner;
    }

    public String getProjectSummary() {
        return projectSummary;
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
                .append(partners)
                .append(leadPartner)
                .append(projectSummary)
                .toHashCode();
    }
}
