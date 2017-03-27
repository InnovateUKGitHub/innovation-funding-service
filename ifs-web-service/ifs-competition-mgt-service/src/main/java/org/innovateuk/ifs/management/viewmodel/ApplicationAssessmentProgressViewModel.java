package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.List;

/**
 * Holder of model attributes for the Application Progress view.
 */
public class ApplicationAssessmentProgressViewModel {

    private long applicationId;
    private String applicationName;
    private String applicationInnovationArea;
    private Long competitionId;
    private String competitionName;
    private boolean inAssessment;
    private String leadOrganisation;
    private List<String> partnerOrganisations;
    private List<ApplicationAssessmentProgressAssignedRowViewModel> assigned;
    private List<ApplicationAvailableAssessorsRowViewModel> available;
    private List<ApplicationAssessmentProgressRejectedRowViewModel> rejected;
    private List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> previouslyAssigned;
    private List<InnovationSectorResource> innovationSectors;
    private Long filterInnovationArea;
    private PaginationViewModel pagination;

    public ApplicationAssessmentProgressViewModel(long applicationId,
                                                  String applicationName,
                                                  String applicationInnovationArea,
                                                  Long competitionId,
                                                  String competitionName,
                                                  boolean inAssessment,
                                                  String leadOrganisation,
                                                  List<String> partnerOrganisations,
                                                  List<ApplicationAssessmentProgressAssignedRowViewModel> assigned,
                                                  List<ApplicationAssessmentProgressRejectedRowViewModel> rejected,
                                                  List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> previouslyAssigned,
                                                  List<ApplicationAvailableAssessorsRowViewModel> available,
                                                  List<InnovationSectorResource> innovationSectors,
                                                  Long filterInnovation,
                                                  PaginationViewModel pagination) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationInnovationArea = applicationInnovationArea;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.inAssessment = inAssessment;
        this.leadOrganisation = leadOrganisation;
        this.partnerOrganisations = partnerOrganisations;
        this.assigned = assigned;
        this.rejected = rejected;
        this.previouslyAssigned = previouslyAssigned;
        this.available = available;
        this.innovationSectors = innovationSectors;
        this.filterInnovationArea = filterInnovation;
        this.pagination = pagination;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationInnovationArea() {
        return applicationInnovationArea;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isInAssessment() {
        return inAssessment;
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

    public List<InnovationSectorResource> getInnovationSectors() {
        return innovationSectors;
    }

    public Long getFilterInnovationArea() {
        return filterInnovationArea;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentProgressViewModel that = (ApplicationAssessmentProgressViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(inAssessment, that.inAssessment)
                .append(applicationName, that.applicationName)
                .append(applicationInnovationArea, that.applicationInnovationArea)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(assigned, that.assigned)
                .append(available, that.available)
                .append(rejected, that.rejected)
                .append(previouslyAssigned, that.previouslyAssigned)
                .append(innovationSectors, that.innovationSectors)
                .append(filterInnovationArea, that.filterInnovationArea)
                .append(pagination, that.pagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(applicationInnovationArea)
                .append(competitionId)
                .append(competitionName)
                .append(inAssessment)
                .append(leadOrganisation)
                .append(partnerOrganisations)
                .append(assigned)
                .append(available)
                .append(rejected)
                .append(previouslyAssigned)
                .append(innovationSectors)
                .append(filterInnovationArea)
                .append(pagination)
                .toHashCode();
    }
}