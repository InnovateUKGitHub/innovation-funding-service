package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.management.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.model.InviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.management.viewmodel.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementInviteAssessorsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementInviteAssessorsController> {

    @Spy
    @InjectMocks
    private InviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Spy
    @InjectMocks
    private InviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Spy
    @InjectMocks
    private InviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    private CompetitionResource competition;

    @Override
    protected CompetitionManagementInviteAssessorsController supplyControllerUnderTest() {
        return new CompetitionManagementInviteAssessorsController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
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
        List<AvailableAssessorResource> availableAssessorResources = setUpAvailableAssessorResources();

        when(competitionInviteRestService.getAvailableAssessors(competition.getId())).thenReturn(restSuccess(availableAssessorResources));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorResources, result);

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getAvailableAssessors(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void invite() throws Exception {
        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(assessorCreatedInviteResources, categoryResources);

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

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void overview() throws Exception {
        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        when(competitionInviteRestService.getInvitationOverview(competition.getId())).thenReturn(restSuccess(assessorInviteOverviewResources));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/overview", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/overview"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInviteOverviews(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getInvitationOverview(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addInviteFromFindView() throws Exception {
        String email = "firstname.lastname@example.com";

        List<AvailableAssessorResource> availableAssessorResources = setUpAvailableAssessorResources();

        ExistingUserStagedInviteResource expectedExistingUserStagedInviteResource = new ExistingUserStagedInviteResource(email, competition.getId());
        when(competitionInviteRestService.inviteUser(expectedExistingUserStagedInviteResource)).thenReturn(restSuccess(newCompetitionInviteResource().build()));
        when(competitionInviteRestService.getAvailableAssessors(competition.getId())).thenReturn(restSuccess(availableAssessorResources));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("add", email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorResources, result);

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).inviteUser(expectedExistingUserStagedInviteResource);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getAvailableAssessors(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeInviteFromFindView() throws Exception {
        String email = "firstname.lastname@example.com";

        List<AvailableAssessorResource> availableAssessorResources = setUpAvailableAssessorResources();

        when(competitionInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());
        when(competitionInviteRestService.getAvailableAssessors(competition.getId())).thenReturn(restSuccess(availableAssessorResources));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("remove", email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorResources, result);

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).deleteInvite(email, competition.getId());
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getAvailableAssessors(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeInviteFromInviteView() throws Exception {
        String email = "firstname.lastname@example.com";

        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();

        when(competitionInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());
        when(competitionInviteRestService.getCreatedInvites(competition.getId())).thenReturn(restSuccess(assessorCreatedInviteResources));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("remove", email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        assertCompetitionDetails(competition, result);
        assertInvitedAssessors(assessorCreatedInviteResources, result);

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).deleteInvite(email, competition.getId());
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addNewUserToInviteView() throws Exception {
        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(assessorCreatedInviteResources, categoryResources);

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

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeNewUserFromInviteView() throws Exception {
        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(assessorCreatedInviteResources, categoryResources);

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

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeNewUserFromInviteView_atLeastOneEmptyRow() throws Exception {
        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = setUpAssessorCreatedInviteResources();
        List<InnovationAreaResource> categoryResources = setupCategoryResources();

        setupDefaultInviteViewExpectations(assessorCreatedInviteResources, categoryResources);

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

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView() throws Exception {
        List<NewUserStagedInviteResource> expectedInvites = newNewUserStagedInviteResource()
                .withEmail("test1@test.com", "test2@test.com")
                .withName("Tester 1", "Tester 2")
                .withInnovationCategoryId(1L)
                .withCompetitionId(competition.getId())
                .build(2);
        NewUserStagedInviteListResource expectedInviteListResource = newNewUserStagedInviteListResource()
                .withInvites(expectedInvites)
                .build();

        when(competitionInviteRestService.inviteNewUsers(expectedInviteListResource, competition.getId()))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1")
                .param("invites[0].email", "test1@test.com")
                .param("invites[0].name", "Tester 1")
                .param("invites[1].email", "test2@test.com")
                .param("invites[1].name", "Tester 2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite", competition.getId())));

        InOrder inOrder = inOrder(competitionService, competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).inviteNewUsers(expectedInviteListResource, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView_noInnovationArea() throws Exception {
        when(competitionInviteRestService.getCreatedInvites(competition.getId())).thenReturn(restSuccess(emptyList()));
        when(categoryServiceMock.getInnovationSectors()).thenReturn(emptyList());

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("invites[0].email", "test@test.com")
                .param("invites[0].name", "Tester"))
                .andExpect(model().hasErrors())
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm returnedForm = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = returnedForm.getBindingResult();

        assertEquals("Please select an innovation area.", bindingResult.getFieldError("selectedInnovationArea").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView_noRows() throws Exception {
        when(competitionInviteRestService.getCreatedInvites(competition.getId())).thenReturn(restSuccess(emptyList()));
        when(categoryServiceMock.getInnovationSectors()).thenReturn(emptyList());

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/assessors/invite", competition.getId())
                .param("inviteNewUsers", "")
                .param("selectedInnovationArea", "1"))
                .andExpect(model().hasErrors())
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteNewAssessorsForm returnedForm = (InviteNewAssessorsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = returnedForm.getBindingResult();

        assertEquals("Please add at least one person to invite.", bindingResult.getFieldError("invites").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsersFromInviteView_invalidRow() throws Exception {
        when(competitionInviteRestService.getCreatedInvites(competition.getId())).thenReturn(restSuccess(emptyList()));
        when(categoryServiceMock.getInnovationSectors()).thenReturn(emptyList());

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

        assertEquals("Please enter a name.", bindingResult.getFieldError("invites[0].name").getDefaultMessage());
        assertEquals("Please enter an email address.", bindingResult.getFieldError("invites[0].email").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId());
        inOrder.verify(categoryServiceMock).getInnovationSectors();
        inOrder.verifyNoMoreInteractions();
    }

    private List<AvailableAssessorResource> setUpAvailableAssessorResources() {
        return newAvailableAssessorResource()
                .withName("Dave Smith", "John Barnes")
                .withInnovationAreas(asList(newInnovationAreaResource()
                        .withName("Earth Observation", "Healthcare, Analytical science")
                        .buildArray(2, InnovationAreaResource.class)))
                .withCompliant(TRUE, FALSE)
                .withEmail("dave@email.com", "john@email.com")
                .withBusinessType(BUSINESS, ACADEMIC)
                .withAdded(TRUE, FALSE)
                .build(2);
    }

    private List<AssessorCreatedInviteResource> setUpAssessorCreatedInviteResources() {
        return newAssessorCreatedInviteResource()
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
                .withName("Dave Smith", "John Barnes")
                .withInnovationAreas(asList(newInnovationAreaResource()
                        .withName("Earth Observation", "Healthcare, Analytical science")
                        .buildArray(2, InnovationAreaResource.class)))
                .withCompliant(TRUE, FALSE)
                .withBusinessType(BUSINESS, ACADEMIC)
                .withStatus(ACCEPTED, REJECTED)
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
        assertEquals(60, model.getAssessorsInvited());
        assertEquals(23, model.getAssessorsAccepted());
        assertEquals(3, model.getAssessorsDeclined());
        assertEquals(6, model.getAssessorsStaged());
    }

    private void assertAvailableAssessors(List<AvailableAssessorResource> expectedAvailableAssessors, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsFindViewModel);
        InviteAssessorsFindViewModel model = (InviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedAvailableAssessors.size(), model.getAssessors().size());

        forEachWithIndex(expectedAvailableAssessors, (i, availableAssessorResource) -> {
            AvailableAssessorRowViewModel availableAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(availableAssessorResource.getName(), availableAssessorRowViewModel.getName());
            assertEquals(availableAssessorResource.getInnovationAreas(), availableAssessorRowViewModel.getInnovationAreas());
            assertEquals(availableAssessorResource.isCompliant(), availableAssessorRowViewModel.isCompliant());
            assertEquals(availableAssessorResource.getEmail(), availableAssessorRowViewModel.getEmail());
            assertEquals(availableAssessorResource.getBusinessType(), availableAssessorRowViewModel.getBusinessType());
            assertEquals(availableAssessorResource.isAdded(), availableAssessorRowViewModel.isAdded());
        });
    }

    private void assertInvitedAssessors(List<AssessorCreatedInviteResource> expectedCreatedInvites, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsInviteViewModel);
        InviteAssessorsInviteViewModel model = (InviteAssessorsInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedCreatedInvites.size(), model.getAssessors().size());

        forEachWithIndex(expectedCreatedInvites, (i, createdInviteResource) -> {
            InvitedAssessorRowViewModel invitedAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(createdInviteResource.getName(), invitedAssessorRowViewModel.getName());
            assertEquals(createdInviteResource.getInnovationAreas(), invitedAssessorRowViewModel.getInnovationAreas());
            assertEquals(createdInviteResource.isCompliant(), invitedAssessorRowViewModel.isCompliant());
            assertEquals(createdInviteResource.getEmail(), invitedAssessorRowViewModel.getEmail());
        });
    }

    private void assertInviteOverviews(List<AssessorInviteOverviewResource> expectedInviteOverviews, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsOverviewViewModel);
        InviteAssessorsOverviewViewModel model = (InviteAssessorsOverviewViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedInviteOverviews.size(), model.getAssessors().size());

        forEachWithIndex(expectedInviteOverviews, (i, inviteOverviewResource) -> {
            OverviewAssessorRowViewModel overviewAssessorRowViewModel = model.getAssessors().get(i);
            assertEquals(inviteOverviewResource.getName(), overviewAssessorRowViewModel.getName());
            assertEquals(inviteOverviewResource.getInnovationAreas(), overviewAssessorRowViewModel.getInnovationAreas());
            assertEquals(inviteOverviewResource.isCompliant(), overviewAssessorRowViewModel.isCompliant());
            assertEquals(inviteOverviewResource.getBusinessType(), overviewAssessorRowViewModel.getBusinessType());
            assertEquals(inviteOverviewResource.getStatus(), overviewAssessorRowViewModel.getStatus());
            assertEquals(inviteOverviewResource.getDetails(), overviewAssessorRowViewModel.getDetails());
        });
    }

    private List<InnovationAreaResource> setupCategoryResources() {
        return newInnovationAreaResource()
                .withName("Innovation Area 1", "Innovation Area 2")
                .build(2);
    }

    private void setupDefaultInviteViewExpectations(List<AssessorCreatedInviteResource> assessorCreatedInviteResources,
                                                    List<InnovationAreaResource> children) {
        List<InnovationSectorResource> innovationSectors = newInnovationSectorResource()
                .withName("Innovation Sector 1")
                .withChildren(children)
                .build(1);

        when(competitionInviteRestService.getCreatedInvites(competition.getId())).thenReturn(restSuccess(assessorCreatedInviteResources));
        when(categoryServiceMock.getInnovationSectors()).thenReturn(innovationSectors);
    }
}
