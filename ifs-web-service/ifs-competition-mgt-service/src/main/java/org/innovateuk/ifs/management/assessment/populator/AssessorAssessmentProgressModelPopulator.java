package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.service.AssessmentPeriodService;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionSummaryRestService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.*;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class AssessorAssessmentProgressModelPopulator {

    @Autowired
    private AssessorCompetitionSummaryRestService assessorCompetitionSummaryRestService;

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private CompetitionRestService competitionService;

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    public AssessorAssessmentProgressViewModel populateModel(long competitionId,
                                                             long assessorId,
                                                             long assessmentPeriodId,
                                                             int page,
                                                             Sort sort,
                                                             String filter,
                                                             boolean isSuperAdmin) {
        AssessorCompetitionSummaryResource summaryResource = assessorCompetitionSummaryRestService
                .getAssessorSummary(assessorId, competitionId)
                .getSuccess();

        List<String> innovationAreas = simpleMap(
                summaryResource.getAssessor().getProfile().getInnovationAreas(),
                CategoryResource::getName
        );

        List<AssessorAssessmentResource> assignedForAssessmentPeriod = summaryResource.getAssignedAssessments().stream()
                .filter(assessmentPeriodFilter(assessmentPeriodId)).collect(toList());

        List<AssessorAssessmentProgressAssignedRowViewModel> assigned =
                getAssignedAssessments(assignedForAssessmentPeriod);

        List<AssessorAssessmentProgressRejectedRowViewModel> rejected =
                getRejectedAssessments(assignedForAssessmentPeriod);

        List<AssessorAssessmentProgressWithdrawnRowViewModel> previouslyAssigned =
                getPreviouslyAssignedAssessments(assignedForAssessmentPeriod);

        ApplicationCountSummaryPageResource applicationCounts = getApplicationCounts(
                competitionId,
                assessorId,
                page,
                filter,
                sort);
        AssessorAssessmentProgressApplicationsViewModel applicationsViewModel = getApplicationsViewModel(
                applicationCounts,
                competitionId,
                sort);

        BusinessType businessType = summaryResource.getAssessor().getProfile().getBusinessType();

        String assessmentPeriodName = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId, summaryResource.getCompetitionId());

        return new AssessorAssessmentProgressViewModel(
                summaryResource.getCompetitionId(),
                summaryResource.getCompetitionName(),
                summaryResource.getCompetitionStatus(),
                getCompetition(summaryResource.getCompetitionId()).isAlwaysOpen(),
                assessorId,
                assessmentPeriodId,
                assessmentPeriodName,
                summaryResource.getAssessor().getUser().getName(),
                innovationAreas,
                filter,
                businessType != null ? businessType.getDisplayName() : "",
                summaryResource.getTotalApplications(),
                applicationCounts.getTotalElements() > SELECTION_LIMIT,
                assigned,
                rejected,
                previouslyAssigned,
                applicationsViewModel,
                isSuperAdmin
        );
    }

    private List<AssessorAssessmentProgressAssignedRowViewModel> getAssignedAssessments(List<AssessorAssessmentResource> assessorAssessments) {
        return assessorAssessments.stream()
                .filter(AssessorAssessmentResource::isAssigned)
                .map(this::getAssessorAssessmentProgressAssignedRowViewModel)
                .collect(toList());
    }

    private AssessorAssessmentProgressAssignedRowViewModel getAssessorAssessmentProgressAssignedRowViewModel(AssessorAssessmentResource assignedAssessment) {
        return new AssessorAssessmentProgressAssignedRowViewModel(
                assignedAssessment.getApplicationId(),
                assignedAssessment.getApplicationName(),
                assignedAssessment.getLeadOrganisation(),
                assignedAssessment.getTotalAssessors(),
                assignedAssessment.getState(),
                assignedAssessment.getAssessmentId()
        );
    }

    private List<AssessorAssessmentProgressRejectedRowViewModel> getRejectedAssessments(List<AssessorAssessmentResource> assessorAssessments) {
        return assessorAssessments.stream()
                .filter(AssessorAssessmentResource::isRejected)
                .map(this::getAssessorAssessmentProgressRejectedRowViewModel)
                .collect(toList());
    }

    private AssessorAssessmentProgressRejectedRowViewModel getAssessorAssessmentProgressRejectedRowViewModel(AssessorAssessmentResource assessment) {

        return  new AssessorAssessmentProgressRejectedRowViewModel(
                assessment.getApplicationId(),
                assessment.getApplicationName(),
                assessment.getLeadOrganisation(),
                assessment.getTotalAssessors(),
                assessment.getRejectReason(),
                assessment.getRejectComment()
        );
    }

    private List<AssessorAssessmentProgressWithdrawnRowViewModel> getPreviouslyAssignedAssessments(List<AssessorAssessmentResource> assessorAssessments) {
        return assessorAssessments.stream()
                .filter(AssessorAssessmentResource::isWithdrawn)
                .map(this::getAssessorAssessmentProgressPreviousAssignedRowViewModel)
                .collect(toList());
    }

    private Predicate<AssessorAssessmentResource> assessmentPeriodFilter(long assessmentPeriodId) {
        return it -> it.getAssessmentPeriodId() != null && it.getAssessmentPeriodId().equals(assessmentPeriodId);
    }

    private AssessorAssessmentProgressWithdrawnRowViewModel getAssessorAssessmentProgressPreviousAssignedRowViewModel(AssessorAssessmentResource assessment) {

        return  new AssessorAssessmentProgressWithdrawnRowViewModel(
                assessment.getApplicationId(),
                assessment.getApplicationName(),
                assessment.getLeadOrganisation(),
                assessment.getTotalAssessors()
        );
    }

    private ApplicationCountSummaryPageResource getApplicationCounts(long competitionId,
                                                                     long assessorId,
                                                                     int page,
                                                                     String filter,
                                                                     Sort sort) {
        return applicationCountSummaryRestService
                .getApplicationCountSummariesByCompetitionIdAndAssessorId(
                        competitionId,
                        assessorId,
                        page,
                        sort,
                        filter)
                .getSuccess();
    }

    private AssessorAssessmentProgressApplicationsViewModel getApplicationsViewModel(ApplicationCountSummaryPageResource applicationCounts,
                                                                                     long competitionId,
                                                                                     Sort sort) {
        CompetitionResource competition = getCompetition(competitionId);

        return new AssessorAssessmentProgressApplicationsViewModel(
                simpleMap(applicationCounts.getContent(), this::getRowViewModel),
                IN_ASSESSMENT == competition.getCompetitionStatus(),
                sort,
                new PaginationViewModel(applicationCounts),
                applicationCounts.getTotalElements());
    }

    private AssessorAssessmentProgressApplicationRowViewModel getRowViewModel(ApplicationCountSummaryResource applicationCount) {
        return new AssessorAssessmentProgressApplicationRowViewModel(applicationCount);
    }

    private CompetitionResource getCompetition(long competitionId) {
        return competitionService.getCompetitionById(competitionId).getSuccess();
    }
}
