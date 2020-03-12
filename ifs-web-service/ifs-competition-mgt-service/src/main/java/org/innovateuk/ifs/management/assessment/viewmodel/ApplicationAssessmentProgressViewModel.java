package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;

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
    private String assessorNameFilter;
    private Sort currentSort;
    private PaginationViewModel pagination;
    private boolean selectAllDisabled;

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
                                                  String assessorNameFilter,
                                                  Sort currentSort,
                                                  PaginationViewModel pagination,
                                                  boolean selectAllDisabled) {
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
        this.assessorNameFilter = assessorNameFilter;
        this.currentSort = currentSort;
        this.pagination = pagination;
        this.selectAllDisabled = selectAllDisabled;
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

    public String getAssessorNameFilter() {
        return assessorNameFilter;
    }

    public Sort getCurrentSort() {
        return currentSort;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
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
                .append(assessorNameFilter, that.assessorNameFilter)
                .append(currentSort, that.currentSort)
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
                .append(assessorNameFilter)
                .append(currentSort)
                .append(pagination)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("applicationId", applicationId)
                .append("applicationName", applicationName)
                .append("applicationInnovationArea", applicationInnovationArea)
                .append("competitionId", competitionId)
                .append("competitionName", competitionName)
                .append("inAssessment", inAssessment)
                .append("leadOrganisation", leadOrganisation)
                .append("partnerOrganisations", partnerOrganisations)
                .append("assigned", assigned)
                .append("available", available)
                .append("rejected", rejected)
                .append("previouslyAssigned", previouslyAssigned)
                .append("innovationSectors", innovationSectors)
                .append("assessorNameFilter", assessorNameFilter)
                .append("currentSort", currentSort)
                .append("pagination", pagination)
                .toString();
    }
}