package org.innovateuk.ifs.review.controller;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.assessor.populator.AssessorProfileModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsAcceptedViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InvitedAssessorRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.review.form.ReviewSelectionForm;
import org.innovateuk.ifs.review.model.ReviewInviteAssessorsAcceptedModelPopulator;
import org.innovateuk.ifs.review.model.ReviewInviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.review.model.ReviewInviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.review.viewmodel.ReviewAvailableAssessorRowViewModel;
import org.innovateuk.ifs.review.viewmodel.ReviewInviteAssessorsFindViewModel;
import org.innovateuk.ifs.review.viewmodel.ReviewInviteAssessorsInviteViewModel;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ReviewInviteAssessorsControllerTest extends BaseControllerMockMVCTest<ReviewInviteAssessorsController> {

    @Spy
    @InjectMocks
    private ReviewInviteAssessorsInviteModelPopulator panelInviteAssessorsInviteModelPopulator;

    @Spy
    @InjectMocks
    private ReviewInviteAssessorsAcceptedModelPopulator panelInviteAssessorsAcceptedModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileModelPopulator assessorProfileModelPopulator;

    @Spy
    @InjectMocks
    private ReviewInviteAssessorsFindModelPopulator panelInviteAssessorsFindModelPopulator;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Mock
    private ReviewInviteRestService reviewInviteRestService;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    private CompetitionResource competition;

    private ReviewInviteStatisticsResource inviteStatistics;

    @Override
    protected ReviewInviteAssessorsController supplyControllerUnderTest() {
        return new ReviewInviteAssessorsController();
    }

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
    public void assessors() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/panel-find", competitionId)));
    }

    @Test
    public void find() throws Exception {
        int page = 2;

        AvailableAssessorPageResource availableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(setUpAvailableAssessorResources())
                .build();

        when(reviewInviteRestService.getAvailableAssessors(competition.getId(), page)).thenReturn(restSuccess(availableAssessorPageResource));
        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(emptyList()));
//        when(competitionKeyStatisticsRestService.getInterviewInviteStatisticsByCompetition(competition.getId())).thenReturn(restSuccess(newInterviewInviteStatisticsResource().build()));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/panel-find"))
                .andReturn();

        ReviewSelectionForm selectionForm = (ReviewSelectionForm) result.getModelAndView().getModel().get("reviewSelectionForm");
        assertTrue(selectionForm.getSelectedAssessorIds().isEmpty());

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);

        InOrder inOrder = inOrder(competitionRestService, reviewInviteRestService, competitionKeyStatisticsRestService);
        inOrder.verify(reviewInviteRestService).getAvailableAssessorIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionKeyStatisticsRestService).getReviewInviteStatisticsByCompetition(competition.getId());
        inOrder.verify(reviewInviteRestService).getAvailableAssessors(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void find_defaultParams() throws Exception {
        int page = 0;

        AvailableAssessorPageResource availableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(emptyList())
                .build();

        when(reviewInviteRestService.getAvailableAssessors(competition.getId(), page)).thenReturn(restSuccess(availableAssessorPageResource));
        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/panel-find"))
                .andReturn();

        ReviewSelectionForm selectionForm = (ReviewSelectionForm) result.getModelAndView().getModel().get("reviewSelectionForm");
        assertTrue(selectionForm.getSelectedAssessorIds().isEmpty());

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);

        InOrder inOrder = inOrder(competitionRestService, reviewInviteRestService, competitionKeyStatisticsRestService);
        inOrder.verify(reviewInviteRestService).getAvailableAssessorIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionKeyStatisticsRestService).getReviewInviteStatisticsByCompetition(competition.getId());
        inOrder.verify(reviewInviteRestService).getAvailableAssessors(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void find_existingCookie() throws Exception {
        int page = 0;
        long expectedAssessorId = 1L;

        ReviewSelectionForm expectedSelectionForm = new ReviewSelectionForm();
        expectedSelectionForm.getSelectedAssessorIds().add(expectedAssessorId);
        Cookie selectionFormCookie = createFormCookie(expectedSelectionForm);

        AvailableAssessorPageResource availableAssessorPageResource = newAvailableAssessorPageResource()
                .withTotalPages(1)
                .withContent(setUpAvailableAssessorResources())
                .build();

        when(reviewInviteRestService.getAvailableAssessors(competition.getId(), page)).thenReturn(restSuccess(availableAssessorPageResource));
        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .cookie(selectionFormCookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/panel-find"))
                .andReturn();

        ReviewSelectionForm selectionForm = (ReviewSelectionForm) result.getModelAndView().getModel().get("reviewSelectionForm");
        assertEquals(expectedSelectionForm, selectionForm);

        Optional<ReviewSelectionForm> resultForm = getreviewSelectionFormFromCookie(result.getResponse(), format("reviewSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedAssessorIds().contains(expectedAssessorId));

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);

        InOrder inOrder = inOrder(competitionRestService, reviewInviteRestService, competitionKeyStatisticsRestService);
        inOrder.verify(reviewInviteRestService).getAvailableAssessorIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionKeyStatisticsRestService).getReviewInviteStatisticsByCompetition(competition.getId());
        inOrder.verify(reviewInviteRestService).getAvailableAssessors(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addAssessorSelectionFromFindView() throws Exception {
        long assessorId = 1L;
        Cookie formCookie = createFormCookie(new ReviewSelectionForm());

        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "true")
                .param("page", "1")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<ReviewSelectionForm> resultForm = getreviewSelectionFormFromCookie(result.getResponse(), format("reviewSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void addAssessorSelectionFromFindView_defaultParams() throws Exception {
        long assessorId = 1L;
        Cookie formCookie = createFormCookie(new ReviewSelectionForm());

        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<ReviewSelectionForm> resultForm = getreviewSelectionFormFromCookie(result.getResponse(), format("reviewSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void addAllAssessorsFromFindView_defaultParams() throws Exception {
        Cookie formCookie = createFormCookie(new ReviewSelectionForm());

        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .param("addAll", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(2)))
                .andExpect(jsonPath("allSelected", is(true)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<ReviewSelectionForm> resultForm = getreviewSelectionFormFromCookie(result.getResponse(), format("reviewSelectionForm_comp_%s", competition.getId()));
        assertEquals(2, resultForm.get().getSelectedAssessorIds().size());
    }

    @Test
    public void removeAssessorSelectionFromFindView() throws Exception {
        long assessorId = 1L;
        ReviewSelectionForm form = new ReviewSelectionForm();
        form.getSelectedAssessorIds().add(assessorId);
        Cookie formCookie = createFormCookie(form);

        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "false")
                .param("page", "1")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<ReviewSelectionForm> resultForm = getreviewSelectionFormFromCookie(result.getResponse(), format("reviewSelectionForm_comp_%s", competition.getId()));
        assertFalse(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void removeAssessorSelectionFromFindView_defaultParams() throws Exception {
        long assessorId = 1L;
        Cookie formCookie;
        ReviewSelectionForm form = new ReviewSelectionForm();
        form.getSelectedAssessorIds().add(assessorId);
        formCookie = createFormCookie(form);

        when(reviewInviteRestService.getAvailableAssessorIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "false")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<ReviewSelectionForm> resultForm = getreviewSelectionFormFromCookie(result.getResponse(), format("reviewSelectionForm_comp_%s", competition.getId()));
        assertFalse(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void invite() throws Exception {
        int page = 0;

        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        AssessorCreatedInvitePageResource assessorCreatedInvitePageResource = newAssessorCreatedInvitePageResource()
                .withContent(assessorCreatedInviteResources)
                .build();

        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(page, assessorCreatedInvitePageResource, categoryResources);

        InviteNewAssessorsForm expectedForm = new InviteNewAssessorsForm();
        expectedForm.setInvites(singletonList(new InviteNewAssessorsRowForm()));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors/invite", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/panel-invite"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInvitedAssessors(assessorCreatedInviteResources, result);

        InOrder inOrder = inOrder(competitionRestService, reviewInviteRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(reviewInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void accepted() throws Exception {
        int page = 1;
        List<ParticipantStatusResource> status = Collections.singletonList(ACCEPTED);

        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        AssessorInviteOverviewPageResource pageResource = newAssessorInviteOverviewPageResource()
                .withContent(assessorInviteOverviewResources)
                .build();

        when(reviewInviteRestService.getInvitationOverview(competition.getId(), page, status))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}/assessors/accepted", competition.getId())
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/panel-accepted"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInviteAccepted(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionRestService, reviewInviteRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(reviewInviteRestService).getInvitationOverview(competition.getId(), page, status);
        inOrder.verifyNoMoreInteractions();
    }

    private void assertInviteAccepted(List<AssessorInviteOverviewResource> expectedInviteAccepted, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsAcceptedViewModel);
        InviteAssessorsAcceptedViewModel model = (InviteAssessorsAcceptedViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedInviteAccepted.size(), model.getAssessors().size());

        forEachWithIndex(expectedInviteAccepted, (i, inviteOverviewResource) -> {
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

    @Test
    public void removeInviteFromInviteView() throws Exception {
        String email = "firstname.lastname@example.com";

        when(reviewInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/invite", competition.getId())
                .param("remove", email)
                .param("page", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/panel/competition/%s/assessors/invite?page=5", competition.getId())))
                .andReturn();

        verify(reviewInviteRestService, only()).deleteInvite(email, competition.getId());
    }

    @Test
    public void removeInviteFromInviteView_defaultParams() throws Exception {
        String email = "firstname.lastname@example.com";

        when(reviewInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/invite", competition.getId())
                .param("remove", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/panel/competition/%s/assessors/invite?page=0", competition.getId())))
                .andReturn();

        verify(reviewInviteRestService, only()).deleteInvite(email, competition.getId());
    }

    @Test
    public void removeAllInvitesFromInviteView() throws Exception {
        when(reviewInviteRestService.deleteAllInvites(competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/panel/competition/{competitionId}/assessors/invite", competition.getId())
                .param("removeAll", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/panel/competition/%s/assessors/invite?page=0", competition.getId())))
                .andReturn();

        verify(reviewInviteRestService).deleteAllInvites(competition.getId());
    }

    private List<AvailableAssessorResource> setUpAvailableAssessorResources() {
        return newAvailableAssessorResource()
                .withId(1L, 2L)
                .withName("Dave Smith", "John Barnes")
                .withInnovationAreas(asList(newInnovationAreaResource()
                        .withName("Earth Observation", "Healthcare, Analytical science")
                        .buildArray(2, InnovationAreaResource.class)))
                .withCompliant(TRUE, FALSE)
                .withEmail("dave@email.com", "john@email.com")
                .withBusinessType(BUSINESS, ACADEMIC)
                .build(2);
    }

    private List<AssessorCreatedInviteResource> setUpAssessorCreatedInviteResources() {
        return newAssessorCreatedInviteResource()
                .withId(1L, 2L)
                .withInviteId(3L, 4L)
                .withName("Dave Smith", "John Barnes")
                .withInnovationAreas(asList(newInnovationAreaResource()
                        .withName("Earth Observation", "Healthcare, Analytical science")
                        .buildArray(2, InnovationAreaResource.class)))
                .withCompliant(TRUE, FALSE)
                .withEmail("dave@email.com", "john@email.com")
                .build(2);
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
                .withStatus(ACCEPTED, ACCEPTED)
                .withDetails("", "")
                .build(2);
    }

    private void assertCompetitionDetails(CompetitionResource expectedCompetition, MvcResult result) {
        InviteAssessorsViewModel model = (InviteAssessorsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals( expectedCompetition.getId(), model.getCompetitionId());
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

    private void assertAvailableAssessors(List<AvailableAssessorResource> expectedAvailableAssessors, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof ReviewInviteAssessorsFindViewModel);
        ReviewInviteAssessorsFindViewModel model = (ReviewInviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedAvailableAssessors.size(), model.getAssessors().size());

        forEachWithIndex(expectedAvailableAssessors, (i, availableAssessorResource) -> {
            ReviewAvailableAssessorRowViewModel availableAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(availableAssessorResource.getName(), availableAssessorRowViewModel.getName());
            assertEquals(formatInnovationAreas(availableAssessorResource.getInnovationAreas()), availableAssessorRowViewModel.getInnovationAreas());
            assertEquals(availableAssessorResource.isCompliant(), availableAssessorRowViewModel.isCompliant());
            assertEquals(availableAssessorResource.getBusinessType(), availableAssessorRowViewModel.getBusinessType());
        });
    }

    private String formatInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        return innovationAreas == null ? EMPTY : innovationAreas.stream()
                .map(CategoryResource::getName)
                .collect(joining(", "));
    }

    private void assertInvitedAssessors(List<AssessorCreatedInviteResource> expectedCreatedInvites, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof ReviewInviteAssessorsInviteViewModel);
        ReviewInviteAssessorsInviteViewModel model = (ReviewInviteAssessorsInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedCreatedInvites.size(), model.getAssessors().size());

        forEachWithIndex(expectedCreatedInvites, (i, createdInviteResource) -> {
            InvitedAssessorRowViewModel invitedAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(createdInviteResource.getName(), invitedAssessorRowViewModel.getName());
            assertEquals(formatInnovationAreas(createdInviteResource.getInnovationAreas()), invitedAssessorRowViewModel.getInnovationAreas());
            assertEquals(createdInviteResource.isCompliant(), invitedAssessorRowViewModel.isCompliant());
            assertEquals(createdInviteResource.getEmail(), invitedAssessorRowViewModel.getEmail());
        });
    }

    private List<InnovationAreaResource> setupCategoryResources() {
        return newInnovationAreaResource()
                .withName("Innovation Area 1", "Innovation Area 2")
                .build(2);
    }

    private void setupDefaultInviteViewExpectations(int page,
                                                    AssessorCreatedInvitePageResource assessorCreatedInvitePageResource,
                                                    List<InnovationAreaResource> children) {
        List<InnovationSectorResource> innovationSectors = newInnovationSectorResource()
                .withName("Innovation Sector 1")
                .withChildren(children)
                .build(1);

        when(reviewInviteRestService.getCreatedInvites(competition.getId(), page)).thenReturn(restSuccess(assessorCreatedInvitePageResource));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(innovationSectors));
    }

    private Cookie createFormCookie(ReviewSelectionForm form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        return new Cookie(format("reviewSelectionForm_comp_%s", competition.getId()), getCompressedString(cookieContent));
    }

    private Optional<ReviewSelectionForm> getreviewSelectionFormFromCookie(MockHttpServletResponse response, String cookieName) throws Exception {
        String value = getDecompressedString(response.getCookie(cookieName).getValue());
        String decodedFormJson  = URLDecoder.decode(value, CharEncoding.UTF_8);

        if (isNotBlank(decodedFormJson)) {
            return Optional.ofNullable(getObjectFromJson(decodedFormJson, ReviewSelectionForm.class));
        } else {
            return Optional.empty();
        }
    }
}
