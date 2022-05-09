package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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
    private PublicContentItemRestService publicContentItemRestService;

    public AssessorCompetitionForPanelDashboardModelPopulator(CompetitionRestService competitionRestService,
                                                              ApplicationService applicationService,
                                                              ReviewRestService reviewRestService,
                                                              OrganisationRestService organisationRestService,
                                                              PublicContentItemRestService publicContentItemRestService) {
        this.competitionRestService = competitionRestService;
        this.applicationService = applicationService;
        this.reviewRestService = reviewRestService;
        this.organisationRestService = organisationRestService;
        this.publicContentItemRestService = publicContentItemRestService;
    }

    public AssessorCompetitionForPanelDashboardViewModel populateModel(Long competitionId, Long userId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        PublicContentItemResource publicContentItem = publicContentItemRestService.getItemByCompetitionId(competitionId).getSuccess();
        ZonedDateTime panelDate = competition.getFundersPanelDate();

        List<AssessorCompetitionForPanelDashboardApplicationViewModel> applications = getApplications(userId, competitionId, publicContentItem);

        return new AssessorCompetitionForPanelDashboardViewModel(
                competition.getId(),
                competition.getName(),
                competition.getLeadTechnologistName(),
                panelDate,
                applications
        );
    }

    private List<AssessorCompetitionForPanelDashboardApplicationViewModel> getApplications(long userId, long competitionId, PublicContentItemResource publicContentItemResource) {
        List<ReviewResource> reviews = reviewRestService.getAssessmentReviews(userId, competitionId).getSuccess();
        return simpleMap(reviews, review -> createApplicationViewModel(review, publicContentItemResource));
    }

    private AssessorCompetitionForPanelDashboardApplicationViewModel createApplicationViewModel(ReviewResource assessmentReview, PublicContentItemResource publicContentItemResource) {
        ApplicationResource application = applicationService.getById(assessmentReview.getApplication());
        OrganisationResource leadOrganisation = organisationRestService.getOrganisationById(application.getLeadOrganisationId()).getSuccess();

        return new AssessorCompetitionForPanelDashboardApplicationViewModel(application.getId(),
                assessmentReview.getId(),
                application.getName(),
                leadOrganisation.getName(),
                assessmentReview.getReviewState(),
                publicContentItemResource.getPublicContentResource().getHash());
    }
}
