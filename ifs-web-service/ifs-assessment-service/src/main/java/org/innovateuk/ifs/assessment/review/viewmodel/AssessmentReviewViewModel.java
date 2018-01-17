package org.innovateuk.ifs.assessment.review.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.SortedSet;

/**
 * Holder of model attributes for the acceptance of an assessment review by an Assessor
 */
public class AssessmentReviewViewModel {

    private long reviewId;
    private long competitionId;
    private String applicationName;
    private SortedSet<OrganisationResource> partners;
    private OrganisationResource leadPartner;
    private String projectSummary;

    public AssessmentReviewViewModel(long assessmentId,
                                     long competitionId,
                                     String applicationName,
                                     SortedSet<OrganisationResource> partners,
                                     OrganisationResource leadPartner,
                                     String projectSummary) {
        this.reviewId = assessmentId;
        this.competitionId = competitionId;
        this.applicationName = applicationName;
        this.partners = partners;
        this.leadPartner = leadPartner;
        this.projectSummary = projectSummary;
    }

    public long getReviewId() {
        return reviewId;
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

        AssessmentReviewViewModel that = (AssessmentReviewViewModel) o;

        return new EqualsBuilder()
                .append(reviewId, that.reviewId)
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
                .append(reviewId)
                .append(competitionId)
                .append(applicationName)
                .append(partners)
                .append(leadPartner)
                .append(projectSummary)
                .toHashCode();
    }
}