package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.management.form.OverviewAssessorsFilterForm;
import org.innovateuk.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsOverviewViewModel;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.OverviewAssessorRowViewModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementInviteAssessorsOverviewControllerTest extends BaseControllerMockMVCTest<CompetitionManagementInviteAssessorsOverviewController> {

    @Spy
    @InjectMocks
    private InviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @Override
    protected CompetitionManagementInviteAssessorsOverviewController supplyControllerUnderTest() {
        return new CompetitionManagementInviteAssessorsOverviewController();
    }

    private CompetitionResource competition;

    private CompetitionInviteStatisticsResource inviteStatistics;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        this.setupCookieUtil();

        competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();

        inviteStatistics = newCompetitionInviteStatisticsResource()
                .withAccepted(46)
                .withInvited(23)
                .withInviteList(10)
                .withDeclined(52)
                .build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionInviteRestService.getInviteStatistics(competition.getId())).thenReturn(restSuccess(inviteStatistics));
    }

    @Test
    public void overview_defaultParams() throws Exception {
        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        AssessorInviteOverviewPageResource pageResource = newAssessorInviteOverviewPageResource()
                .withContent(assessorInviteOverviewResources)
                .build();

        List<Long> inviteIds = asList(1L, 2L);

        when(categoryRestServiceMock.getInnovationAreas()).thenReturn(restSuccess(newInnovationAreaResource().build(4)));
        when(competitionInviteRestService.getInvitationOverview(competition.getId(), 0, empty(), empty(), empty()))
                .thenReturn(restSuccess(pageResource));
        when(competitionInviteRestService.getAssessorsNotAcceptedInviteIds(competition.getId(), empty(), empty(), empty())).thenReturn(restSuccess(inviteIds));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/overview", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/overview"))
                .andReturn();

        OverviewAssessorsFilterForm filterForm = (OverviewAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");

        assertEquals(empty(), filterForm.getCompliant());
        assertEquals(empty(), filterForm.getInnovationArea());
        assertEquals(empty(), filterForm.getStatus());

        assertCompetitionDetails(competition, result);
        assertInviteOverviews(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionRestService, categoryRestServiceMock, competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).getAssessorsNotAcceptedInviteIds(competition.getId(), empty(), empty(), empty());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationAreas();
        inOrder.verify(competitionInviteRestService).getInvitationOverview(competition.getId(), 0, empty(), empty(), empty());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void overview() throws Exception {
        int page = 1;
        Optional<Long> innovationArea = of(10L);
        Optional<Boolean> compliant = of(TRUE);
        List<Long> inviteIds = asList(1L, 2L);

        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        AssessorInviteOverviewPageResource pageResource = newAssessorInviteOverviewPageResource()
                .withContent(assessorInviteOverviewResources)
                .build();

        when(categoryRestServiceMock.getInnovationAreas()).thenReturn(restSuccess(newInnovationAreaResource().build(4)));
        when(competitionInviteRestService.getInvitationOverview(competition.getId(), page, innovationArea, empty(), compliant))
                .thenReturn(restSuccess(pageResource));
        when(competitionInviteRestService.getAssessorsNotAcceptedInviteIds(competition.getId(),innovationArea, empty(),compliant)).thenReturn(restSuccess(inviteIds));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/overview", competition.getId())
                .param("page", "1")
                .param("innovationArea", "10")
                .param("compliant", "TRUE"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/overview"))
                .andReturn();

        OverviewAssessorsFilterForm filterForm = (OverviewAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");

        assertEquals(of(TRUE), filterForm.getCompliant());
        assertEquals(of(10L), filterForm.getInnovationArea());

        assertCompetitionDetails(competition, result);
        assertInviteOverviews(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionRestService, categoryRestServiceMock, competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).getAssessorsNotAcceptedInviteIds(competition.getId(), innovationArea, empty(), compliant);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationAreas();
        inOrder.verify(competitionInviteRestService).getInvitationOverview(competition.getId(), page, innovationArea, empty(), compliant);
        inOrder.verifyNoMoreInteractions();
    }

    private List<AssessorInviteOverviewResource> setUpAssessorInviteOverviewResources() {
        return newAssessorInviteOverviewResource()
                .withName("Dave Smith", "John Barnes")
                .withInnovationAreas(asList(newInnovationAreaResource()
                        .withName("Earth Observation", "Healthcare, Analytical science")
                        .buildArray(2, InnovationAreaResource.class)))
                .withCompliant(TRUE, FALSE)
                .withBusinessType(BUSINESS, ACADEMIC)
                .withStatus(PENDING, REJECTED)
                .withDetails("", "Invite declined as person is too busy")
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
        assertEquals(inviteStatistics.getInviteList(), model.getAssessorsStaged());
    }

    private void assertInviteOverviews(List<AssessorInviteOverviewResource> expectedInviteOverviews, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsOverviewViewModel);
        InviteAssessorsOverviewViewModel model = (InviteAssessorsOverviewViewModel) result.getModelAndView().getModel().get("model");

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
