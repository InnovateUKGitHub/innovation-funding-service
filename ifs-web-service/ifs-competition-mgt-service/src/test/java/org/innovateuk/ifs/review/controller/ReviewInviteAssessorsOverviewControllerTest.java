package org.innovateuk.ifs.review.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.review.model.ReviewInviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.review.viewmodel.ReviewInviteAssessorsOverviewViewModel;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.innovateuk.ifs.organisation.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.organisation.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReviewInviteAssessorsOverviewControllerTest extends BaseControllerMockMVCTest<ReviewInviteAssessorsOverviewController> {

    @Spy
    @InjectMocks
    private ReviewInviteAssessorsOverviewModelPopulator panelInviteAssessorsOverviewModelPopulator;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Mock
    private ReviewInviteRestService reviewInviteRestService;

    @Override
    protected ReviewInviteAssessorsOverviewController supplyControllerUnderTest() {
        return new ReviewInviteAssessorsOverviewController();
    }

    private CompetitionResource competition;

    private ReviewInviteStatisticsResource inviteStatistics;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        setupCookieUtil(cookieUtil);

        competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();

        inviteStatistics = newReviewInviteStatisticsResource()
                .withAssessorsInvited(5)
                .withAssessorsAccepted(1)
                .withAssessorsRejected(1)
                .build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionKeyStatisticsRestService.getReviewInviteStatisticsByCompetition(competition.getId())).thenReturn(restSuccess(inviteStatistics));
    }

    @Test
    public void overview() throws Exception {
        int page = 1;
        List<Long> inviteIds = asList(1L, 2L);

        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        AssessorInviteOverviewPageResource pageResource = newAssessorInviteOverviewPageResource()
                .withContent(assessorInviteOverviewResources)
                .build();

        when(reviewInviteRestService.getInvitationOverview(competition.getId(), page, asList(REJECTED, PENDING)))
                .thenReturn(restSuccess(pageResource));
        when(reviewInviteRestService.getNonAcceptedAssessorInviteIds(competition.getId())).thenReturn(restSuccess(inviteIds));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors/pending-and-declined", competition.getId())
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/panel-overview"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInviteOverviews(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionRestService, reviewInviteRestService);
        inOrder.verify(reviewInviteRestService).getNonAcceptedAssessorInviteIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(reviewInviteRestService).getInvitationOverview(competition.getId(), page, asList(REJECTED, PENDING));
        inOrder.verifyNoMoreInteractions();
    }

    private List<AssessorInviteOverviewResource> setUpAssessorInviteOverviewResources() {
        return newAssessorInviteOverviewResource()
                .withId(1L, 2L)
                .withInviteId(3L, 4L)
                .withName("Dave Smith", "John Barnes")
                .withInnovationAreas(asList(newInnovationAreaResource()
                        .withName("Earth Observation", "Healthcare, Analytical science")
                        .buildArray(2, InnovationAreaResource.class)))
                .withCompliant(TRUE, FALSE)
                .withBusinessType(BUSINESS, ACADEMIC)
                .withStatus(PENDING, REJECTED)
                .withDetails("", "Invite declined: person is too busy")
                .build(2);
    }

    private void assertCompetitionDetails(CompetitionResource expectedCompetition, MvcResult result) {
        InviteAssessorsViewModel model = (InviteAssessorsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedCompetition.getId(), model.getCompetitionId());
        assertEquals(expectedCompetition.getName(), model.getCompetitionName());
        assertInnovationSectorAndArea(expectedCompetition, model);
        assertStatistics(model);
    }

    private void assertInnovationSectorAndArea(CompetitionResource expectedCompetition, InviteAssessorsViewModel model) {
        assertEquals(expectedCompetition.getInnovationSectorName(), model.getInnovationSector());
        assertEquals(StringUtils.join(expectedCompetition.getInnovationAreaNames(), ", "), model.getInnovationArea());
    }

    private void assertStatistics(InviteAssessorsViewModel model) {
        assertEquals(inviteStatistics.getInvited(), model.getAssessorsInvited());
        assertEquals(inviteStatistics.getAccepted(), model.getAssessorsAccepted());
        assertEquals(inviteStatistics.getDeclined(), model.getAssessorsDeclined());
    }

    private void assertInviteOverviews(List<AssessorInviteOverviewResource> expectedInviteOverviews, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof ReviewInviteAssessorsOverviewViewModel);
        ReviewInviteAssessorsOverviewViewModel model = (ReviewInviteAssessorsOverviewViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedInviteOverviews.size(), model.getAssessors().size());

        forEachWithIndex(expectedInviteOverviews, (i, inviteOverviewResource) -> {
            OverviewAssessorRowViewModel overviewAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(inviteOverviewResource.getName(), overviewAssessorRowViewModel.getName());
            assertEquals(formatInnovationAreas(inviteOverviewResource.getInnovationAreas()), overviewAssessorRowViewModel.getInnovationAreas());
            assertEquals(inviteOverviewResource.isCompliant(), overviewAssessorRowViewModel.isCompliant());
            assertEquals(inviteOverviewResource.getBusinessType(), overviewAssessorRowViewModel.getBusinessType());
            assertEquals(inviteOverviewResource.getStatus(), overviewAssessorRowViewModel.getStatus());
            assertEquals(inviteOverviewResource.getDetails(), overviewAssessorRowViewModel.getDetails());
            assertEquals(inviteOverviewResource.getInviteId(), overviewAssessorRowViewModel.getInviteId());
        });
    }

    private String formatInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        return innovationAreas == null ? EMPTY : innovationAreas.stream()
                .map(CategoryResource::getName)
                .collect(joining(", "));
    }
}
