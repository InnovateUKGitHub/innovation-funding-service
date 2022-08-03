package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionForPanelDashboardModelPopulator;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.innovateuk.ifs.review.resource.ReviewState.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = { "classpath:application.yml", "classpath:/application-web-core.properties"} )
public class AssessorCompetitionForPanelDashboardControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionForPanelDashboardController> {

    @Spy
    @InjectMocks
    private AssessorCompetitionForPanelDashboardModelPopulator assessorCompetitionForPanelDashboardModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ReviewRestService reviewRestService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Override
    protected AssessorCompetitionForPanelDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionForPanelDashboardController();
    }

    @Test
    public void competitionForPanelDashboard() throws Exception {
        long userId = 1L;

        CompetitionResource competition = buildTestCompetition();
        List<ApplicationResource> applications = buildTestApplications();

        List<ReviewResource> assessmentReviews = newReviewResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .withActivityState(PENDING, ACCEPTED, REJECTED, CONFLICT_OF_INTEREST)
                .build(4);

        List<OrganisationResource> organisations = buildTestOrganisations();

        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItem));
        when(reviewRestService.getAssessmentReviews(userId, competition.getId())).thenReturn(restSuccess(assessmentReviews));

        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}/panel", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-for-panel-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionRestService, reviewRestService, applicationService, organisationRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(reviewRestService).getAssessmentReviews(userId, competition.getId());

        assessmentReviews.forEach(assessmentReview -> {
            inOrder.verify(applicationService).getById(assessmentReview.getApplication());
            inOrder.verify(organisationRestService).getOrganisationById(anyLong());
        });

        inOrder.verifyNoMoreInteractions();

        List<AssessorCompetitionForPanelDashboardApplicationViewModel> expectedReviews = asList(
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(0).getId(), assessmentReviews.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName(), assessmentReviews.get(0).getReviewState(), null),
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(1).getId(), assessmentReviews.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName(), assessmentReviews.get(1).getReviewState(), null),
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(2).getId(), assessmentReviews.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName(), assessmentReviews.get(2).getReviewState(), null),
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(3).getId(), assessmentReviews.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName(), assessmentReviews.get(3).getReviewState(), null)
        );

        AssessorCompetitionForPanelDashboardViewModel model = (AssessorCompetitionForPanelDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Innovation Lead", model.getLeadTechnologist());
        assertEquals(competition.getFundersPanelDate(), model.getPanelDate());
        assertEquals(expectedReviews, model.getApplications());
    }

    @Test
    public void competitionDashboard_empty() throws Exception {
        long userId = 1L;

        CompetitionResource competition = buildTestCompetition();

        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(reviewRestService.getAssessmentReviews(userId, competition.getId())).thenReturn(restSuccess(emptyList()));
        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItem));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}/panel", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-for-panel-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionRestService, reviewRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(reviewRestService).getAssessmentReviews(userId, competition.getId());
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(applicationService);
        verifyNoInteractions(organisationRestService);

        AssessorCompetitionForPanelDashboardViewModel model = (AssessorCompetitionForPanelDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Innovation Lead", model.getLeadTechnologist());
        assertEquals(competition.getFundersPanelDate(), model.getPanelDate());
        assertTrue(model.getApplications().isEmpty());
    }

    private CompetitionResource buildTestCompetition() {
        ZonedDateTime assessorAcceptsDate = ZonedDateTime.now().minusDays(2);
        ZonedDateTime assessorDeadlineDate = ZonedDateTime.now().plusDays(4);

        return newCompetitionResource()
                .withName("Juggling Craziness")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Innovation Lead")
                .withAssessorAcceptsDate(assessorAcceptsDate)
                .withAssessorDeadlineDate(assessorDeadlineDate)
                .build();
    }

    private List<ApplicationResource> buildTestApplications() {
        return newApplicationResource()
                .withId(11L, 12L, 13L, 14L)
                .withName("Juggling is fun", "Juggling is very fun", "Juggling is not fun", "Juggling is word that sounds funny to say")
                .withLeadOrganisationId(1L, 2L, 3L, 4L)
                .build(4);
    }

    private List<OrganisationResource> buildTestOrganisations() {
        return newOrganisationResource()
                .withId(1L, 2L, 3L, 4L)
                .withName("The Best Juggling Company", "Juggle Ltd", "Jugglez Ltd", "Mo Juggling Mo Problems Ltd")
                .build(4);
    }
}
