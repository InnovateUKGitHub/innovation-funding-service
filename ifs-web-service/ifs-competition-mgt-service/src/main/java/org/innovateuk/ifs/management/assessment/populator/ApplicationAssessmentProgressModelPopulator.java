package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.application.service.ApplicationAssessmentSummaryRestService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.*;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Application Progress view.
 */
@Component
public class ApplicationAssessmentProgressModelPopulator {

    @Autowired
    private ApplicationAssessmentSummaryRestService applicationAssessmentSummaryRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    public ApplicationAssessmentProgressViewModel populateModel(Long applicationId, String assessorNameFilter, int page, Sort sort) {
        ApplicationAssessmentSummaryResource applicationAssessmentSummary = applicationAssessmentSummaryRestService
                .getApplicationAssessmentSummary(applicationId).getSuccess();

        List<ApplicationAssessorResource> notAvailableAssessors = applicationAssessmentSummaryRestService.getAssignedAssessors(applicationId).getSuccess();
        ApplicationAvailableAssessorPageResource availableAssessors = applicationAssessmentSummaryRestService.getAvailableAssessors(applicationId, page, 20, assessorNameFilter, sort).getSuccess();

        return new ApplicationAssessmentProgressViewModel(applicationAssessmentSummary.getId(),
                applicationAssessmentSummary.getName(),
                applicationAssessmentSummary.getInnovationArea(),
                applicationAssessmentSummary.getCompetitionId(),
                applicationAssessmentSummary.getCompetitionName(),
                IN_ASSESSMENT == applicationAssessmentSummary.getCompetitionStatus(),
                applicationAssessmentSummary.getLeadOrganisation(),
                applicationAssessmentSummary.getPartnerOrganisations(),
                getAssignedAssessors(notAvailableAssessors),
                getRejectedAssessors(notAvailableAssessors),
                getPreviouslyAssignedAssessors(notAvailableAssessors),
                getAvailableAssessors(availableAssessors.getContent()),
                getInnovationSectors(),
                assessorNameFilter,
                sort,
                new PaginationViewModel(availableAssessors),
                availableAssessors.getTotalElements() > SELECTION_LIMIT);
    }

    private List<InnovationSectorResource> getInnovationSectors() {
        return categoryRestService.getInnovationSectors().getSuccess();
    }

    private List<ApplicationAssessmentProgressAssignedRowViewModel> getAssignedAssessors(List<ApplicationAssessorResource> assessors) {
        return assessors.stream()
                .filter(ApplicationAssessorResource::isAssigned)
                .map(this::getAssignedRowViewModel)
                .collect(toList());
    }

    private List<ApplicationAssessmentProgressRejectedRowViewModel> getRejectedAssessors(List<ApplicationAssessorResource> assessors) {
        return assessors.stream()
                .filter(ApplicationAssessorResource::isRejected)
                .map(this::getRejectedRowViewModel)
                .collect(toList());
    }

    private List<ApplicationAssessmentProgressPreviouslyAssignedRowViewModel> getPreviouslyAssignedAssessors(
            List<ApplicationAssessorResource> assessors) {
        return assessors.stream()
                .filter(ApplicationAssessorResource::isWithdrawn)
                .map(this::getPreviouslyAssignedRowViewModel)
                .collect(toList());
    }

    private ApplicationAssessmentProgressAssignedRowViewModel getAssignedRowViewModel(ApplicationAssessorResource applicationAssessorResource) {
        return new ApplicationAssessmentProgressAssignedRowViewModel(
                applicationAssessorResource.getUserId(),
                applicationAssessorResource.getFirstName() + " " + applicationAssessorResource.getLastName(),
                applicationAssessorResource.getTotalApplicationsCount(),
                applicationAssessorResource.getAssignedCount(),
                applicationAssessorResource.getBusinessType(),
                simpleMap(applicationAssessorResource.getInnovationAreas(), CategoryResource::getName),
                applicationAssessorResource.isNotified(),
                applicationAssessorResource.isAccepted(),
                applicationAssessorResource.isStarted(),
                applicationAssessorResource.isSubmitted(),
                applicationAssessorResource.getMostRecentAssessmentId()
        );
    }

    private ApplicationAssessmentProgressRejectedRowViewModel getRejectedRowViewModel(
            ApplicationAssessorResource applicationAssessorResource) {
        return new ApplicationAssessmentProgressRejectedRowViewModel(
                applicationAssessorResource.getUserId(),
                applicationAssessorResource.getFirstName() + " " + applicationAssessorResource.getLastName(),
                applicationAssessorResource.getTotalApplicationsCount(),
                applicationAssessorResource.getAssignedCount(),
                applicationAssessorResource.getBusinessType(),
                simpleMap(applicationAssessorResource.getInnovationAreas(), CategoryResource::getName),
                applicationAssessorResource.getRejectReason(),
                applicationAssessorResource.getRejectComment()
        );
    }

    private ApplicationAssessmentProgressPreviouslyAssignedRowViewModel getPreviouslyAssignedRowViewModel(
            ApplicationAssessorResource applicationAssessorResource) {
        return new ApplicationAssessmentProgressPreviouslyAssignedRowViewModel(
                applicationAssessorResource.getUserId(),
                applicationAssessorResource.getFirstName() + " " + applicationAssessorResource.getLastName(),
                applicationAssessorResource.getTotalApplicationsCount(),
                applicationAssessorResource.getAssignedCount(),
                applicationAssessorResource.getBusinessType(),
                simpleMap(applicationAssessorResource.getInnovationAreas(), CategoryResource::getName));
    }

    private List<ApplicationAvailableAssessorsRowViewModel> getAvailableAssessors(List<ApplicationAvailableAssessorResource> assessors) {
        return assessors.stream().map(this::getAvailableRowViewModel).collect(toList());
    }

    private ApplicationAvailableAssessorsRowViewModel getAvailableRowViewModel(ApplicationAvailableAssessorResource applicationAssessorResource) {
        return new ApplicationAvailableAssessorsRowViewModel(
                applicationAssessorResource.getUserId(),
                applicationAssessorResource.getFirstName() + " " + applicationAssessorResource.getLastName(),
                defaultString(applicationAssessorResource.getSkillAreas()),
                applicationAssessorResource.getTotalApplicationsCount(),
                applicationAssessorResource.getAssignedCount(),
                applicationAssessorResource.getSubmittedCount()
        );
    }
}