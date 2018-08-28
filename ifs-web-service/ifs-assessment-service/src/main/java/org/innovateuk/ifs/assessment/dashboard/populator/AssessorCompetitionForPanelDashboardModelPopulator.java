package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
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

    private CompetitionRestService competitionRestService;
    private ApplicationService applicationService;
    private ReviewRestService reviewRestService;
    private OrganisationRestService organisationRestService;

    public AssessorCompetitionForPanelDashboardModelPopulator(CompetitionRestService competitionRestService,
                                                              ApplicationService applicationService,
                                                              ReviewRestService reviewRestService,
                                                              OrganisationRestService organisationRestService) {
        this.competitionRestService = competitionRestService;
        this.applicationService = applicationService;
        this.reviewRestService = reviewRestService;
        this.organisationRestService = organisationRestService;
    }

    public AssessorCompetitionForPanelDashboardViewModel populateModel(Long competitionId, Long userId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
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
        OrganisationResource leadOrganisation = organisationRestService.getOrganisationById(application.getLeadOrganisationId()).getSuccess();

        return new AssessorCompetitionForPanelDashboardApplicationViewModel(application.getId(),
                assessmentReview.getId(),
                application.getName(),
                leadOrganisation.getName(),
                assessmentReview.getReviewState());
    }
}
