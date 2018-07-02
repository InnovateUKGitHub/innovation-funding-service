package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.viewmodel.UserApplicationRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Assessor Panel Dashboard view.
 */
@Component
public class AssessorCompetitionForPanelDashboardModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ReviewRestService reviewRestService;

    public AssessorCompetitionForPanelDashboardViewModel populateModel(Long competitionId, Long userId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        ZonedDateTime panelDate = competition.getFundersPanelDate();

        List<AssessorCompetitionForPanelDashboardApplicationViewModel> applications = getApplications(userId, competitionId);

        return new AssessorCompetitionForPanelDashboardViewModel(
                competition.getId(),
                competition.getName(),
                competition.getLeadTechnologistName(),
                panelDate,
                applications
        );
    }

    private List<AssessorCompetitionForPanelDashboardApplicationViewModel> getApplications(long userId, long competitionId) {
        List<ReviewResource> reviews = reviewRestService.getAssessmentReviews(userId, competitionId).getSuccess();
        return simpleMap(reviews, review -> createApplicationViewModel(review));
    }

    private AssessorCompetitionForPanelDashboardApplicationViewModel createApplicationViewModel(ReviewResource assessmentReview) {
        ApplicationResource application = applicationService.getById(assessmentReview.getApplication());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);

        return new AssessorCompetitionForPanelDashboardApplicationViewModel(application.getId(),
                assessmentReview.getId(),
                application.getName(),
                leadOrganisation.map(OrganisationResource::getName).orElse(EMPTY),
                assessmentReview.getReviewState());
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }
}
