package org.innovateuk.ifs.management.assessment.controller;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.management.assessment.controller.InviteAssessorsController;
import org.innovateuk.ifs.management.assessor.form.AssessorSelectionForm;
import org.innovateuk.ifs.management.assessor.form.FindAssessorsFilterForm;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.assessor.populator.*;
import org.innovateuk.ifs.management.assessor.viewmodel.*;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

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
import static java.util.Optional.empty;
import static java.util.Optional.of;
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
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
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

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InviteAssessorsControllerTest extends BaseControllerMockMVCTest<InviteAssessorsController> {

    @Spy
    @InjectMocks
    private CompetitionInviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Spy
    @InjectMocks
    private CompetitionInviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Spy
    @InjectMocks
    private CompetitionInviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @Spy
    @InjectMocks
    private CompetitionInviteAssessorsAcceptedModelPopulator inviteAssessorsAcceptedModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionInviteRestService competitionInviteRestService;

    private CompetitionResource competition;

    private CompetitionInviteStatisticsResource inviteStatistics;

    @Override
    protected InviteAssessorsController supplyControllerUnderTest() {
        return new InviteAssessorsController();
    }

    @Before
    public void setUp() {
        setupCookieUtil(cookieUtil);

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
    public void assessors() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(get("/competition/{competitionId}/assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/find", competitionId)));
    }

    @Test
    public void find() throws Exception {
        int page = 2;
        Optional<Long> innovationArea = of(3L);

        AvailableAssessorPageResource availableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(setUpAvailableAssessorResources())
                .build();
        List<InnovationSectorResource> expectedInnovationSectorOptions = newInnovationSectorResource().build(4);

        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(expectedInnovationSectorOptions));
        when(competitionInviteRestService.getAvailableAssessors(competition.getId(), page, innovationArea)).thenReturn(restSuccess(availableAssessorPageResource));
        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), innovationArea)).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId())
                .param("page", "2")
                .param("innovationArea", "3"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        FindAssessorsFilterForm filterForm = (FindAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");
        assertEquals(of(3L), filterForm.getInnovationArea());
        AssessorSelectionForm selectionForm = (AssessorSelectionForm) result.getModelAndView().getModel().get("assessorSelectionForm");
        assertTrue(selectionForm.getSelectedAssessorIds().isEmpty());

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);
        assertFindFilterOptionsAreCorrect(expectedInnovationSectorOptions, result);

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getAvailableAssessorIds(competition.getId(), innovationArea);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verify(competitionInviteRestService).getAvailableAssessors(competition.getId(), page, innovationArea);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void find_defaultParams() throws Exception {
        int page = 0;
        Optional<Long> innovationArea = empty();

        AvailableAssessorPageResource availableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(emptyList())
                .build();
        List<InnovationSectorResource> expectedInnovationSectorOptions = newInnovationSectorResource().build(4);

        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(expectedInnovationSectorOptions));
        when(competitionInviteRestService.getAvailableAssessors(competition.getId(), page, innovationArea)).thenReturn(restSuccess(availableAssessorPageResource));
        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), innovationArea)).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        FindAssessorsFilterForm filterForm = (FindAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");
        assertEquals(empty(), filterForm.getInnovationArea());
        AssessorSelectionForm selectionForm = (AssessorSelectionForm) result.getModelAndView().getModel().get("assessorSelectionForm");
        assertTrue(selectionForm.getSelectedAssessorIds().isEmpty());

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);
        assertFindFilterOptionsAreCorrect(expectedInnovationSectorOptions, result);

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getAvailableAssessorIds(competition.getId(), innovationArea);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verify(competitionInviteRestService).getAvailableAssessors(competition.getId(), page, innovationArea);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void find_existingCookie() throws Exception {
        int page = 0;
        Optional<Long> innovationArea = empty();
        long expectedAssessorId = 1L;
        AssessorSelectionForm expectedSelectionForm = new AssessorSelectionForm();
        expectedSelectionForm.getSelectedAssessorIds().add(expectedAssessorId);
        Cookie selectionFormCookie = createFormCookie(expectedSelectionForm);

        AvailableAssessorPageResource availableAssessorPageResource = newAvailableAssessorPageResource()
                .withTotalPages(1)
                .withContent(setUpAvailableAssessorResources())
                .build();
        List<InnovationSectorResource> expectedInnovationSectorOptions = newInnovationSectorResource().build(4);

        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(expectedInnovationSectorOptions));
        when(competitionInviteRestService.getAvailableAssessors(competition.getId(), page, innovationArea)).thenReturn(restSuccess(availableAssessorPageResource));
        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), innovationArea)).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId())
                .cookie(selectionFormCookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        FindAssessorsFilterForm filterForm = (FindAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");
        assertEquals(empty(), filterForm.getInnovationArea());
        AssessorSelectionForm selectionForm = (AssessorSelectionForm) result.getModelAndView().getModel().get("assessorSelectionForm");
        assertEquals(expectedSelectionForm, selectionForm);

        Optional<AssessorSelectionForm> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("assessorSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedAssessorIds().contains(expectedAssessorId));

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);
        assertFindFilterOptionsAreCorrect(expectedInnovationSectorOptions, result);

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getAvailableAssessorIds(competition.getId(), innovationArea);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verify(competitionInviteRestService).getAvailableAssessors(competition.getId(), page, innovationArea);
        inOrder.verifyNoMoreInteractions();
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

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/invite", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInvitedAssessors(assessorCreatedInviteResources, result);

        InviteNewAssessorsForm form = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        assertFalse(form.isVisible());

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addAssessorSelectionFromFindView() throws Exception {
        long assessorId = 1L;
        Optional<Long> innovationArea = of(4L);
        Cookie formCookie = createFormCookie(new AssessorSelectionForm());

        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), innovationArea)).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "true")
                .param("page", "1")
                .param("innovationArea", "4")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<AssessorSelectionForm> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("assessorSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void addAssessorSelectionFromFindView_defaultParams() throws Exception {
        long assessorId = 1L;
        Cookie formCookie = createFormCookie(new AssessorSelectionForm());

        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<AssessorSelectionForm> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("assessorSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void addAllAssessorsFromFindView_defaultParams() throws Exception {
        Cookie formCookie = createFormCookie(new AssessorSelectionForm());

        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("addAll", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(2)))
                .andExpect(jsonPath("allSelected", is(true)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<AssessorSelectionForm> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("assessorSelectionForm_comp_%s", competition.getId()));
        assertEquals(2, resultForm.get().getSelectedAssessorIds().size());
    }

    @Test
    public void removeAssessorSelectionFromFindView() throws Exception {
        long assessorId = 1L;
        AssessorSelectionForm form = new AssessorSelectionForm();
        form.getSelectedAssessorIds().add(assessorId);
        Cookie formCookie = createFormCookie(form);

        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), of(4L))).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "false")
                .param("page", "1")
                .param("innovationArea", "4")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<AssessorSelectionForm> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("assessorSelectionForm_comp_%s", competition.getId()));
        assertFalse(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void removeAssessorSelectionFromFindView_defaultParams() throws Exception {
        long assessorId = 1L;
        Cookie formCookie;
        AssessorSelectionForm form = new AssessorSelectionForm();
        form.getSelectedAssessorIds().add(assessorId);
        formCookie = createFormCookie(form);

        when(competitionInviteRestService.getAvailableAssessorIds(competition.getId(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("selectionId", valueOf(assessorId))
                .param("isSelected", "false")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<AssessorSelectionForm> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("assessorSelectionForm_comp_%s", competition.getId()));
        assertFalse(resultForm.get().getSelectedAssessorIds().contains(assessorId));
    }

    @Test
    public void removeInviteFromInviteView() throws Exception {
        String email = "firstname.lastname@example.com";

        when(competitionInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("remove", email)
                .param("page", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite?page=5", competition.getId())))
                .andReturn();

        verify(competitionInviteRestService, only()).deleteInvite(email, competition.getId());
    }

    @Test
    public void removeInviteFromInviteView_defaultParams() throws Exception {
        String email = "firstname.lastname@example.com";

        when(competitionInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("remove", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite?page=0", competition.getId())))
                .andReturn();

        verify(competitionInviteRestService, only()).deleteInvite(email, competition.getId());
    }

    @Test
    public void removeAllInvitesFromInviteView() throws Exception {
        when(competitionInviteRestService.deleteAllInvites(competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("removeAll", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite?page=0", competition.getId())))
                .andReturn();

        verify(competitionInviteRestService).deleteAllInvites(competition.getId());
    }

    @Test
    public void addNewUserToInviteView() throws Exception {
        int page = 0;

        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        AssessorCreatedInvitePageResource assessorCreatedInvitePageResource = newAssessorCreatedInvitePageResource()
                .withContent(assessorCreatedInviteResources)
                .build();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(page, assessorCreatedInvitePageResource, categoryResources);

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("addNewUser", "submit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeExists("form"))
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm form = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        InviteNewAssessorsRowForm expectedNewUserRow = new InviteNewAssessorsRowForm();

        assertTrue(form.isVisible());
        assertEquals(1, form.getInvites().size());
        assertEquals(expectedNewUserRow, form.getInvites().get(0));

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeNewUserFromInviteView() throws Exception {
        int page = 0;

        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        AssessorCreatedInvitePageResource assessorCreatedInvitePageResource = newAssessorCreatedInvitePageResource()
                .withContent(assessorCreatedInviteResources)
                .build();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(page, assessorCreatedInvitePageResource, categoryResources);

        InviteNewAssessorsRowForm newUserRow1 = new InviteNewAssessorsRowForm();
        newUserRow1.setName("Tester 1");
        newUserRow1.setEmail("test1@test.com");

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("removeNewUser", "0")
                .param("invites[0].email", "test1@test.com")
                .param("invites[0].name", "Tester 1")
                .param("invites[1].email", "test2@test.com")
                .param("invites[1].name", "Tester 2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeExists("form"))
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm form = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");

        InviteNewAssessorsRowForm expectedNewUserRow = new InviteNewAssessorsRowForm();
        expectedNewUserRow.setName("Tester 2");
        expectedNewUserRow.setEmail("test2@test.com");

        assertTrue(form.isVisible());
        assertEquals(1, form.getInvites().size());
        assertEquals(expectedNewUserRow, form.getInvites().get(0));

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeNewUserFromInviteView_atLeastOneEmptyRow() throws Exception {
        int page = 0;

        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        AssessorCreatedInvitePageResource assessorCreatedInvitePageResource = newAssessorCreatedInvitePageResource()
                .withContent(assessorCreatedInviteResources)
                .build();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(page, assessorCreatedInvitePageResource, categoryResources);

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("removeNewUser", "0")
                .param("invites[0].email", "test1@test.com")
                .param("invites[0].name", "Tester 1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeExists("form"))
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm form = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        InviteNewAssessorsRowForm expectedInviteRow = new InviteNewAssessorsRowForm();

        assertTrue(form.isVisible());
        assertEquals(1, form.getInvites().size());
        assertEquals(expectedInviteRow, form.getInvites().get(0));

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView() throws Exception {
        NewUserStagedInviteListResource expectedInviteListResource = newNewUserStagedInviteListResource()
                .withInvites(
                        newNewUserStagedInviteResource()
                                .withEmail("test1@test.com", "test2@test.com")
                                .withName("Tester One", "Tester Two")
                                .withInnovationAreaId(1L)
                                .withCompetitionId(competition.getId())
                                .build(2)
                )
                .build();

        when(competitionInviteRestService.inviteNewUsers(expectedInviteListResource, competition.getId()))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1")
                .param("invites[0].email", "test1@test.com")
                .param("invites[0].name", "Tester One")
                .param("invites[1].email", "test2@test.com")
                .param("invites[1].name", "Tester Two")
                .param("page", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite?page=5", competition.getId())));

        verify(competitionInviteRestService, only()).inviteNewUsers(expectedInviteListResource, competition.getId());
    }

    @Test
    public void inviteNewUsersFromInviteView_defaultParams() throws Exception {
        NewUserStagedInviteListResource expectedInviteListResource = newNewUserStagedInviteListResource()
                .withInvites(
                        newNewUserStagedInviteResource()
                                .withEmail("test1@test.com")
                                .withName("Tester One")
                                .withInnovationAreaId(1L)
                                .withCompetitionId(competition.getId())
                                .build(1)
                )
                .build();

        when(competitionInviteRestService.inviteNewUsers(expectedInviteListResource, competition.getId()))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1")
                .param("invites[0].email", "test1@test.com")
                .param("invites[0].name", "Tester One"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite?page=0", competition.getId())));

        verify(competitionInviteRestService, only()).inviteNewUsers(expectedInviteListResource, competition.getId());
    }

    @Test
    public void inviteNewUsersFromInviteView_noInnovationArea() throws Exception {
        int page = 0;

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(emptyList())
                .build();

        when(competitionInviteRestService.getCreatedInvites(competition.getId(), page)).thenReturn(restSuccess(expectedPageResource));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("invites[0].email", "test@test.com")
                .param("invites[0].name", "Tester"))
                .andExpect(model().hasErrors())
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm returnedForm = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = returnedForm.getBindingResult();

        assertEquals("Please enter an innovation sector and area.", bindingResult.getFieldError("selectedInnovationArea").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView_noRows() throws Exception {
        int page = 0;

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(emptyList())
                .build();

        when(competitionInviteRestService.getCreatedInvites(competition.getId(), page)).thenReturn(restSuccess(expectedPageResource));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1"))
                .andExpect(model().hasErrors())
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm returnedForm = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = returnedForm.getBindingResult();

        assertEquals("Please add at least one person to invite.", bindingResult.getFieldError("invites").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView_invalidRow() throws Exception {
        int page = 0;

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(emptyList())
                .build();

        when(competitionInviteRestService.getCreatedInvites(competition.getId(), page)).thenReturn(restSuccess(expectedPageResource));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1")
                .param("invites[0].email", "")
                .param("invites[0].name", ""))
                .andExpect(model().hasErrors())
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm returnedForm = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = returnedForm.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(3, bindingResult.getFieldErrorCount());

        assertEquals(2, bindingResult.getFieldErrorCount("invites[0].name"));
        assertEquals(1, bindingResult.getFieldErrorCount("invites[0].email"));

        assertTrue(bindingResult.getFieldErrors("invites[0].name").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("The name should have at least {2} characters.")));
        assertTrue(bindingResult.getFieldErrors("invites[0].name").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Please enter a name.")));
        assertTrue(bindingResult.getFieldErrors("invites[0].email").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Please enter an email address.")));

        InOrder inOrder = inOrder(competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView_invalidNameAndEmail() throws Exception {
        int page = 0;

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(emptyList())
                .build();

        when(competitionInviteRestService.getCreatedInvites(competition.getId(), page)).thenReturn(restSuccess(expectedPageResource));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1")
                .param("invites[0].email", "invalid")
                .param("invites[0].name", "1234"))
                .andExpect(model().hasErrors())
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm returnedForm = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = returnedForm.getBindingResult();

        assertEquals("Please enter a valid name.", bindingResult.getFieldError("invites[0].name").getDefaultMessage());
        assertEquals("Please enter a valid email address.", bindingResult.getFieldError("invites[0].email").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
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

        when(competitionInviteRestService.getInvitationOverview(competition.getId(), page, empty(), status, empty()))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/accepted", competition.getId())
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/accepted"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInviteAccepted(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionRestService,  competitionInviteRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(competitionInviteRestService).getInvitationOverview(competition.getId(), page, empty(), status, empty());
        inOrder.verifyNoMoreInteractions();
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
                .withStatus(ACCEPTED, REJECTED)
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

    private void assertAvailableAssessors(List<AvailableAssessorResource> expectedAvailableAssessors, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof CompetitionInviteAssessorsFindViewModel);
        CompetitionInviteAssessorsFindViewModel model = (CompetitionInviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedAvailableAssessors.size(), model.getAssessors().size());

        forEachWithIndex(expectedAvailableAssessors, (i, availableAssessorResource) -> {
            CompetitionAvailableAssessorRowViewModel availableAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(availableAssessorResource.getName(), availableAssessorRowViewModel.getName());
            assertEquals(formatInnovationAreas(availableAssessorResource.getInnovationAreas()), availableAssessorRowViewModel.getInnovationAreas());
            assertEquals(availableAssessorResource.isCompliant(), availableAssessorRowViewModel.isCompliant());
            assertEquals(availableAssessorResource.getEmail(), availableAssessorRowViewModel.getEmail());
            assertEquals(availableAssessorResource.getBusinessType(), availableAssessorRowViewModel.getBusinessType());
        });
    }

    private String formatInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        return innovationAreas == null ? EMPTY : innovationAreas.stream()
                .map(CategoryResource::getName)
                .collect(joining(", "));
    }

    private void assertInvitedAssessors(List<AssessorCreatedInviteResource> expectedCreatedInvites, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof CompetitionInviteAssessorsInviteViewModel);
        CompetitionInviteAssessorsInviteViewModel model = (CompetitionInviteAssessorsInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedCreatedInvites.size(), model.getAssessors().size());

        forEachWithIndex(expectedCreatedInvites, (i, createdInviteResource) -> {
            InvitedAssessorRowViewModel invitedAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(createdInviteResource.getName(), invitedAssessorRowViewModel.getName());
            assertEquals(formatInnovationAreas(createdInviteResource.getInnovationAreas()), invitedAssessorRowViewModel.getInnovationAreas());
            assertEquals(createdInviteResource.isCompliant(), invitedAssessorRowViewModel.isCompliant());
            assertEquals(createdInviteResource.getEmail(), invitedAssessorRowViewModel.getEmail());
        });
    }

    private void assertFindFilterOptionsAreCorrect(List<InnovationSectorResource> expectedInnovationSectorOptions, MvcResult result) {
        CompetitionInviteAssessorsFindViewModel viewModel = (CompetitionInviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedInnovationSectorOptions, viewModel.getInnovationSectorOptions());
    }

    private void assertInviteOverviews(List<AssessorInviteOverviewResource> expectedInviteOverviews, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof CompetitionInviteAssessorsOverviewViewModel);
        CompetitionInviteAssessorsOverviewViewModel model = (CompetitionInviteAssessorsOverviewViewModel) result.getModelAndView().getModel().get("model");

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

        when(competitionInviteRestService.getCreatedInvites(competition.getId(), page)).thenReturn(restSuccess(assessorCreatedInvitePageResource));
        when(categoryRestServiceMock.getInnovationSectors()).thenReturn(restSuccess(innovationSectors));
    }

    private Cookie createFormCookie(AssessorSelectionForm form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        return new Cookie(format("assessorSelectionForm_comp_%s", competition.getId()), getCompressedString(cookieContent));
    }

    private Optional<AssessorSelectionForm> getAssessorSelectionFormFromCookie(MockHttpServletResponse response, String cookieName) throws Exception {
        String value = getDecompressedString(response.getCookie(cookieName).getValue());
        String decodedFormJson  = URLDecoder.decode(value, CharEncoding.UTF_8);

        if (isNotBlank(decodedFormJson)) {
            return Optional.ofNullable(getObjectFromJson(decodedFormJson, AssessorSelectionForm.class));
        } else {
            return Optional.empty();
        }
    }
}
