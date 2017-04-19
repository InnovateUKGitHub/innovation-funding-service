package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.management.form.FindAssessorsFilterForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.form.OverviewAssessorsFilterForm;
import org.innovateuk.ifs.management.model.AssessorProfileModelPopulator;
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
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
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

    @Spy
    @InjectMocks
    private AssessorProfileModelPopulator assessorProfileModelPopulator;

    private CompetitionResource competition;

    private CompetitionInviteStatisticsResource inviteStatistics;

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

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId())
                .param("page", "2")
                .param("innovationArea", "3"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        FindAssessorsFilterForm filterForm = (FindAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");
        assertEquals(of(3L), filterForm.getInnovationArea());

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);
        assertFindFilterOptionsAreCorrect(expectedInnovationSectorOptions, result);

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
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

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        FindAssessorsFilterForm filterForm = (FindAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");

        assertEquals(empty(), filterForm.getInnovationArea());

        assertCompetitionDetails(competition, result);
        assertAvailableAssessors(availableAssessorPageResource.getContent(), result);
        assertFindFilterOptionsAreCorrect(expectedInnovationSectorOptions, result);

        InOrder inOrder = inOrder(competitionRestService, competitionInviteRestService, categoryRestServiceMock);
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
    public void overview() throws Exception {
        int page = 1;
        Optional<Long> innovationArea = of(10L);
        Optional<ParticipantStatusResource> status = of(ACCEPTED);
        Optional<Boolean> compliant = of(TRUE);

        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        AssessorInviteOverviewPageResource pageResource = newAssessorInviteOverviewPageResource()
                .withContent(assessorInviteOverviewResources)
                .build();

        when(categoryRestServiceMock.getInnovationAreas()).thenReturn(restSuccess(newInnovationAreaResource().build(4)));
        when(competitionInviteRestService.getInvitationOverview(competition.getId(), page, innovationArea, status, compliant))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/overview", competition.getId())
                .param("page", "1")
                .param("innovationArea", "10")
                .param("status", "ACCEPTED")
                .param("compliant", "TRUE"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/overview"))
                .andReturn();

        OverviewAssessorsFilterForm filterForm = (OverviewAssessorsFilterForm) result.getModelAndView().getModel().get("filterForm");

        assertEquals(of(TRUE), filterForm.getCompliant());
        assertEquals(of(10L), filterForm.getInnovationArea());
        assertEquals(of(ACCEPTED), filterForm.getStatus());

        assertCompetitionDetails(competition, result);
        assertInviteOverviews(assessorInviteOverviewResources, result);

        InOrder inOrder = inOrder(competitionRestService, categoryRestServiceMock, competitionInviteRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationAreas();
        inOrder.verify(competitionInviteRestService).getInvitationOverview(competition.getId(), page, innovationArea, status, compliant);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void overview_defaultParams() throws Exception {
        List<AssessorInviteOverviewResource> assessorInviteOverviewResources = setUpAssessorInviteOverviewResources();

        AssessorInviteOverviewPageResource pageResource = newAssessorInviteOverviewPageResource()
                .withContent(assessorInviteOverviewResources)
                .build();

        when(categoryRestServiceMock.getInnovationAreas()).thenReturn(restSuccess(newInnovationAreaResource().build(4)));
        when(competitionInviteRestService.getInvitationOverview(competition.getId(), 0, empty(), empty(), empty()))
                .thenReturn(restSuccess(pageResource));

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
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(categoryRestServiceMock).getInnovationAreas();
        inOrder.verify(competitionInviteRestService).getInvitationOverview(competition.getId(), 0, empty(), empty(), empty());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addInviteFromFindView() throws Exception {
        String email = "firstname.lastname@example.com";

        ExistingUserStagedInviteResource expectedExistingUserStagedInviteResource = new ExistingUserStagedInviteResource(email, competition.getId());

        when(competitionInviteRestService.inviteUser(expectedExistingUserStagedInviteResource))
                .thenReturn(restSuccess(newCompetitionInviteResource().build()));

        mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("add", email)
                .param("page", "1")
                .param("innovationArea", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/find?page=1&innovationArea=4", competition.getId())));

        verify(competitionInviteRestService, only()).inviteUser(expectedExistingUserStagedInviteResource);
    }

    @Test
    public void addInviteFromFindView_defaultParams() throws Exception {
        String email = "firstname.lastname@example.com";

        ExistingUserStagedInviteResource expectedExistingUserStagedInviteResource = new ExistingUserStagedInviteResource(email, competition.getId());

        when(competitionInviteRestService.inviteUser(expectedExistingUserStagedInviteResource))
                .thenReturn(restSuccess(newCompetitionInviteResource().build()));

        mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("add", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/find?page=0", competition.getId())));

        verify(competitionInviteRestService, only()).inviteUser(expectedExistingUserStagedInviteResource);
    }

    @Test
    public void removeInviteFromFindView() throws Exception {
        String email = "firstname.lastname@example.com";

        when(competitionInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("remove", email)
                .param("page", "1")
                .param("innovationArea", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/find?page=1&innovationArea=4", competition.getId())));

        verify(competitionInviteRestService, only()).deleteInvite(email, competition.getId());
    }

    @Test
    public void removeInviteFromFindView_defaultParams() throws Exception {
        String email = "firstname.lastname@example.com";

        when(competitionInviteRestService.deleteInvite(email, competition.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/find", competition.getId())
                .param("remove", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/find?page=0", competition.getId())));

        verify(competitionInviteRestService, only()).deleteInvite(email, competition.getId());
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
                                .withName("Tester 1", "Tester 2")
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
                .param("invites[0].name", "Tester 1")
                .param("invites[1].email", "test2@test.com")
                .param("invites[1].name", "Tester 2")
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
                                .withName("Tester 1")
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
                .param("invites[0].name", "Tester 1"))
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

        assertEquals("Please enter a name.", bindingResult.getFieldError("invites[0].name").getDefaultMessage());
        assertEquals("Please enter an email address.", bindingResult.getFieldError("invites[0].email").getDefaultMessage());

        InOrder inOrder = inOrder(competitionInviteRestService, categoryRestServiceMock);
        inOrder.verify(competitionInviteRestService).getCreatedInvites(competition.getId(), page);
        inOrder.verify(categoryRestServiceMock).getInnovationSectors();
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
        assertEquals(inviteStatistics.getInvited(), model.getAssessorsInvited());
        assertEquals(inviteStatistics.getAccepted(), model.getAssessorsAccepted());
        assertEquals(inviteStatistics.getDeclined(), model.getAssessorsDeclined());
        assertEquals(inviteStatistics.getInviteList(), model.getAssessorsStaged());
    }

    private void assertAvailableAssessors(List<AvailableAssessorResource> expectedAvailableAssessors, MvcResult result) {
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsFindViewModel);
        InviteAssessorsFindViewModel model = (InviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedAvailableAssessors.size(), model.getAssessors().size());

        forEachWithIndex(expectedAvailableAssessors, (i, availableAssessorResource) -> {
            AvailableAssessorRowViewModel availableAssessorRowViewModel = model.getAssessors().get(i);
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
        assertTrue(result.getModelAndView().getModel().get("model") instanceof InviteAssessorsInviteViewModel);
        InviteAssessorsInviteViewModel model = (InviteAssessorsInviteViewModel) result.getModelAndView().getModel().get("model");

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
        InviteAssessorsFindViewModel viewModel = (InviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedInnovationSectorOptions, viewModel.getInnovationSectorOptions());
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
}
