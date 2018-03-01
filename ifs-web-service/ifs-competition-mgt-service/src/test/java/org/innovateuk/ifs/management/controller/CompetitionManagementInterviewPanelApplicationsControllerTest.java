package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationResource;
import org.innovateuk.ifs.management.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.form.PanelSelectionForm;
import org.innovateuk.ifs.management.model.AssessorProfileModelPopulator;
import org.innovateuk.ifs.management.model.InterviewPanelApplicationsFindModelPopulator;
import org.innovateuk.ifs.management.model.InterviewPanelApplicationsInviteModelPopulator;
import org.innovateuk.ifs.management.viewmodel.*;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationPageResourceBuilder.newAvailableApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationResourceBuilder.newAvailableApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInviteResourceBuilder.newInterviewPanelStagedApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelStagedApplicationPageResourceBuilder.newInterviewPanelStagedApplicationPageResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementInterviewPanelApplicationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementInterviewPanelApplicationsController> {

    @Spy
    @InjectMocks
    private InterviewPanelApplicationsFindModelPopulator interviewPanelApplicationsFindModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileModelPopulator assessorProfileModelPopulator;

    @Spy
    @InjectMocks
    private InterviewPanelApplicationsInviteModelPopulator interviewPanelApplicationsInviteModelPopulator;

    private CompetitionResource competition;

    @Override
    protected CompetitionManagementInterviewPanelApplicationsController supplyControllerUnderTest() {
        return new CompetitionManagementInterviewPanelApplicationsController();
    }

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

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
    }

    @Test
    public void find() throws Exception {
        int page = 2;

        AvailableApplicationPageResource availableAssessorPageResource = newAvailableApplicationPageResource()
                .withContent(newAvailableApplicationResource().build(2))
                .build();

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(singletonList(1L)));
        when(interviewPanelRestService.getAvailableApplications(competition.getId(), page)).thenReturn(restSuccess(availableAssessorPageResource));

        MvcResult result = mockMvc.perform(get("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview-panel-find"))
                .andReturn();

        PanelSelectionForm selectionForm = (PanelSelectionForm) result.getModelAndView().getModel().get("interviewPanelApplicationSelectionForm");
        assertTrue(selectionForm.getSelectedIds().isEmpty());

        assertCompetitionDetails(competition, result);
        assertAvailableApplications(availableAssessorPageResource.getContent(), result);

        InOrder inOrder = inOrder(competitionRestService, interviewPanelRestService);
        inOrder.verify(interviewPanelRestService).getAvailableApplicationIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(interviewPanelRestService).getAvailableApplications(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void find_defaultParams() throws Exception {
        int page = 0;

        AvailableApplicationPageResource availableAssessorPageResource = newAvailableApplicationPageResource()
                .withContent(newAvailableApplicationResource().build(2))
                .build();

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(singletonList(1L)));
        when(interviewPanelRestService.getAvailableApplications(competition.getId(), page)).thenReturn(restSuccess(availableAssessorPageResource));

        MvcResult result = mockMvc.perform(get("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview-panel-find"))
                .andReturn();

        PanelSelectionForm selectionForm = (PanelSelectionForm) result.getModelAndView().getModel().get("interviewPanelApplicationSelectionForm");
        assertTrue(selectionForm.getSelectedIds().isEmpty());

        assertCompetitionDetails(competition, result);
        assertAvailableApplications(availableAssessorPageResource.getContent(), result);

        InOrder inOrder = inOrder(competitionRestService, interviewPanelRestService);
        inOrder.verify(interviewPanelRestService).getAvailableApplicationIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(interviewPanelRestService).getAvailableApplications(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void find_existingCookie() throws Exception {
        int page = 0;
        long expectedApplicationId = 1L;

        PanelSelectionForm expectedSelectionForm = new PanelSelectionForm();
        expectedSelectionForm.getSelectedIds().add(expectedApplicationId);
        Cookie selectionFormCookie = createFormCookie(expectedSelectionForm);

        AvailableApplicationPageResource availableAssessorPageResource = newAvailableApplicationPageResource()
                .withContent(newAvailableApplicationResource().build(2))
                .build();

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));
        when(interviewPanelRestService.getAvailableApplications(competition.getId(), page)).thenReturn(restSuccess(availableAssessorPageResource));

        MvcResult result = mockMvc.perform(get("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .cookie(selectionFormCookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview-panel-find"))
                .andReturn();

        PanelSelectionForm selectionForm = (PanelSelectionForm) result.getModelAndView().getModel().get("interviewPanelApplicationSelectionForm");
        assertEquals(expectedSelectionForm, selectionForm);

        Optional<PanelSelectionForm> resultForm = getPanelSelectionFormFromCookie(result.getResponse(), format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedIds().contains(expectedApplicationId));

        assertCompetitionDetails(competition, result);
        assertAvailableApplications(availableAssessorPageResource.getContent(), result);

        InOrder inOrder = inOrder(competitionRestService, interviewPanelRestService);
        inOrder.verify(interviewPanelRestService).getAvailableApplicationIds(competition.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(interviewPanelRestService).getAvailableApplications(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addApplicationSelectionFromFindView() throws Exception {
        long applicationId = 1L;
        Cookie formCookie = createFormCookie(new PanelSelectionForm());

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .param("selectionId", valueOf(applicationId))
                .param("isSelected", "true")
                .param("page", "1")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<PanelSelectionForm> resultForm = getPanelSelectionFormFromCookie(result.getResponse(), format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedIds().contains(applicationId));
    }

    @Test
    public void addApplicationSelectionFromFindView_defaultParams() throws Exception {
        long applicationId = 1L;
        Cookie formCookie = createFormCookie(new PanelSelectionForm());

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .param("selectionId", valueOf(applicationId))
                .param("isSelected", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<PanelSelectionForm> resultForm = getPanelSelectionFormFromCookie(result.getResponse(), format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()));
        assertTrue(resultForm.get().getSelectedIds().contains(applicationId));
    }

    @Test
    public void addAllApplicationsFromFindView_defaultParams() throws Exception {
        Cookie formCookie = createFormCookie(new PanelSelectionForm());

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .param("addAll", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(2)))
                .andExpect(jsonPath("allSelected", is(true)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<PanelSelectionForm> resultForm = getPanelSelectionFormFromCookie(result.getResponse(), format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()));
        assertEquals(2, resultForm.get().getSelectedIds().size());
    }

    @Test
    public void removeApplicationSelectionFromFindView() throws Exception {
        long applicationId = 1L;
        PanelSelectionForm form = new PanelSelectionForm();
        form.getSelectedIds().add(applicationId);
        Cookie formCookie = createFormCookie(form);

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .param("selectionId", valueOf(applicationId))
                .param("isSelected", "false")
                .param("page", "1")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<PanelSelectionForm> resultForm = getPanelSelectionFormFromCookie(result.getResponse(), format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()));
        assertFalse(resultForm.get().getSelectedIds().contains(applicationId));
    }

    @Test
    public void removeApplicationSelectionFromFindView_defaultParams() throws Exception {
        long applicationId = 1L;
        Cookie formCookie;
        PanelSelectionForm form = new PanelSelectionForm();
        form.getSelectedIds().add(applicationId);
        formCookie = createFormCookie(form);

        when(interviewPanelRestService.getAvailableApplicationIds(competition.getId())).thenReturn(restSuccess(asList(1L, 2L)));

        MvcResult result = mockMvc.perform(post("/assessment/interview-panel/competition/{competitionId}/applications/find", competition.getId())
                .param("selectionId", valueOf(applicationId))
                .param("isSelected", "false")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andExpect(jsonPath("limitExceeded", is(false)))
                .andReturn();

        Optional<PanelSelectionForm> resultForm = getPanelSelectionFormFromCookie(result.getResponse(), format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()));
        assertFalse(resultForm.get().getSelectedIds().contains(applicationId));
    }

    @Test
    public void invite() throws Exception {
        int page = 0;

        List<InterviewPanelStagedApplicationResource> interviewPanelStagedApplicationResources = setUpApplicationCreatedInviteResources();
        InterviewPanelStagedApplicationPageResource interviewPanelStagedApplicationPageResource = newInterviewPanelStagedApplicationPageResource()
                .withContent(interviewPanelStagedApplicationResources)
                .build();

        setupDefaultInviteViewExpectations(page, interviewPanelStagedApplicationPageResource);

        InviteNewAssessorsForm expectedForm = new InviteNewAssessorsForm();
        expectedForm.setInvites(singletonList(new InviteNewAssessorsRowForm()));

        MvcResult result = mockMvc.perform(get("/assessment/interview-panel/competition/{competitionId}/applications/invite", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview-panel-invite"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInvitedApplications(interviewPanelStagedApplicationResources, result);

        InOrder inOrder = inOrder(competitionRestService, interviewPanelRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(interviewPanelRestService).getStagedApplications(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();
    }

    private List<InterviewPanelStagedApplicationResource> setUpApplicationCreatedInviteResources() {
        return newInterviewPanelStagedApplicationResource()
                .withId(1L, 2L)
                .withApplicationId(3L, 4L)
                .withApplicationName("App 1", "App 2")
                .withLeadOrganisationName("Org 1", "Org 2")
                .build(2);
    }

    private void assertCompetitionDetails(CompetitionResource expectedCompetition, MvcResult result) {
        InterviewPanelApplicationsViewModel model = (InterviewPanelApplicationsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedCompetition.getId(), (Long) model.getCompetitionId());
        assertEquals(expectedCompetition.getName(), model.getCompetitionName());
        assertInnovationSectorAndArea(expectedCompetition, model);
    }

    private void assertInnovationSectorAndArea(CompetitionResource expectedCompetition, InterviewPanelApplicationsViewModel model) {
        assertEquals(expectedCompetition.getInnovationSectorName(), model.getInnovationSector());
        assertEquals(StringUtils.join(expectedCompetition.getInnovationAreaNames(), ", "), model.getInnovationArea());
    }

    private void assertAvailableApplications(List<AvailableApplicationResource> expectedAvailableApplications, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InterviewPanelApplicationsFindViewModel);
        InterviewPanelApplicationsFindViewModel model = (InterviewPanelApplicationsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedAvailableApplications.size(), model.getApplications().size());

        forEachWithIndex(expectedAvailableApplications, (i, availableApplicationResource) -> {
            InterviewPanelApplicationRowViewModel availableAssessorRowViewModel = model.getApplications().get(i);
            assertEquals(availableApplicationResource.getName(), availableAssessorRowViewModel.getName());
        });
    }

    private String formatInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        return innovationAreas == null ? EMPTY : innovationAreas.stream()
                .map(CategoryResource::getName)
                .collect(joining(", "));
    }

    private void assertInvitedApplications(List<InterviewPanelStagedApplicationResource> expectedCreatedInvites, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InterviewPanelApplicationsInviteViewModel);
        InterviewPanelApplicationsInviteViewModel model = (InterviewPanelApplicationsInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedCreatedInvites.size(), model.getApplications().size());

        forEachWithIndex(expectedCreatedInvites, (i, createdInviteResource) -> {
            InterviewPanelApplicationInviteRowViewModel invitedApplicationRowViewModel = model.getApplications().get(i);
            assertEquals(createdInviteResource.getApplicationName(), invitedApplicationRowViewModel.getApplicationName());
            assertEquals(createdInviteResource.getLeadOrganisationName(), invitedApplicationRowViewModel.getLeadOrganisation());
        });
    }

    private void setupDefaultInviteViewExpectations(int page,
                                                    InterviewPanelStagedApplicationPageResource interviewPanelStagedApplicationPageResource) {

        when(interviewPanelRestService.getStagedApplications(competition.getId(), page)).thenReturn(restSuccess(interviewPanelStagedApplicationPageResource));
    }

    private Cookie createFormCookie(PanelSelectionForm form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        return new Cookie(format("interviewPanelApplicationSelectionForm_comp_%s", competition.getId()), getCompressedString(cookieContent));
    }

    private Optional<PanelSelectionForm> getPanelSelectionFormFromCookie(MockHttpServletResponse response, String cookieName) throws Exception {
        String value = getDecompressedString(response.getCookie(cookieName).getValue());
        String decodedFormJson  = URLDecoder.decode(value, CharEncoding.UTF_8);

        if (isNotBlank(decodedFormJson)) {
            return Optional.ofNullable(getObjectFromJson(decodedFormJson, PanelSelectionForm.class));
        } else {
            return Optional.empty();
        }
    }
}
