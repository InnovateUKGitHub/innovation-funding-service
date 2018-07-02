package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.form.InterviewAllocationNotifyForm;
import org.innovateuk.ifs.interview.form.InterviewAllocationSelectionForm;
import org.innovateuk.ifs.interview.model.AllocatedInterviewApplicationsModelPopulator;
import org.innovateuk.ifs.interview.model.InterviewAcceptedAssessorsModelPopulator;
import org.innovateuk.ifs.interview.model.UnallocatedInterviewApplicationsModelPopulator;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAllocateApplicationsViewModel;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.application.populator.AllocateInterviewApplicationsModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.InterviewAcceptedAssessorsRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InterviewAcceptedAssessorsViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder.newInterviewNotifyAllocationResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InterviewAllocationControllerTest extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Spy
    @InjectMocks
    private InterviewAcceptedAssessorsModelPopulator interviewAcceptedAssessorsModelPopulator;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private InterviewAllocationRestService interviewAllocationRestService;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessorRestService assessorRestService;

    @Spy
    @InjectMocks
    private UnallocatedInterviewApplicationsModelPopulator unallocatedInterviewApplicationsModelPopulator;

    @Spy
    @InjectMocks
    private AllocateInterviewApplicationsModelPopulator allocateInterviewApplicationsModelPopulator;

    @Spy
    @InjectMocks
    private AllocatedInterviewApplicationsModelPopulator allocatedInterviewApplicationsModelPopulator;

    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
    }

    @Mock
    private CookieUtil cookieUtil;

    @Before
    public void setUp() {
        super.setUp();
        setupCookieUtil(cookieUtil);
    }

    @Test
    public void overview() throws Exception {
        int page = 0;

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withName("Competition x")
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);

        InterviewAcceptedAssessorsResource interviewAcceptedAssessorsResource = newInterviewAcceptedAssessorsResource()
                .build();

        InterviewAcceptedAssessorsRowViewModel assessors = new InterviewAcceptedAssessorsRowViewModel(interviewAcceptedAssessorsResource);

        InterviewAcceptedAssessorsPageResource pageResource = newInterviewAcceptedAssessorsPageResource()
                .withContent(singletonList(interviewAcceptedAssessorsResource))
                .build();

        when(interviewAllocationRestService.getInterviewAcceptedAssessors(competition.getId(), page)).thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}/assessors/allocate-assessors", competition.getId())
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview/allocate-accepted-assessors"))
                .andReturn();

        InterviewAcceptedAssessorsViewModel model = (InterviewAcceptedAssessorsViewModel) result.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(competitionService, interviewAllocationRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(interviewAllocationRestService).getInterviewAcceptedAssessors(competition.getId(), page);
        inOrder.verifyNoMoreInteractions();

        assertEquals((long) competition.getId(), model.getCompetitionId());
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(singletonList(assessors), model.getAssessors());
    }

    @Test
    public void applications() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withName("Competition x")
                .build();

        UserResource user = newUserResource()
                .withId(1L)
                .withFirstName("Kieran")
                .withLastName("Hester")
                .build();

        List<InnovationAreaResource> innovationAreas = newInnovationAreaResource()
                .withSector(1L, 2L)
                .withSectorName("Sector1", "Sector2")
                .build(2);

        ProfileResource profile = newProfileResource()
                .withInnovationAreas(innovationAreas)
                .withSkillsAreas("Skills")
                .withBusinessType(BusinessType.ACADEMIC)
                .build();

        AssessorProfileResource assessorProfile = newAssessorProfileResource()
                .withUser(user)
                .withProfile(profile)
                .build();

        InterviewApplicationPageResource pageResource = newInterviewApplicationPageResource()
                .withAllocatedApplications(1L)
                .withUnallocatedApplications(2L)
                .withContent(newInterviewApplicationResource()
                    .withId(1L, 2L)
                    .withLeadOrganisation("Lead 1", "Lead 2")
                    .withNumberOfAssessors(1L, 2L)
                    .build(2))
                .build();

        when(userRestServiceMock.retrieveUserById(user.getId())).thenReturn(restSuccess(user));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(assessorProfile));
        when(interviewAllocationRestService.getUnallocatedApplications(competition.getId(), user.getId(), 0)).thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}/assessors/unallocated-applications/{userId}", competition.getId(), user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview/unallocated-applications"))
                .andReturn();

        InterviewAssessorApplicationsViewModel model = (InterviewAssessorApplicationsViewModel) result.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(competitionRestService, userRestServiceMock, assessorRestService);
        inOrder.verify(userRestServiceMock).retrieveUserById(user.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(assessorRestService).getAssessorProfile(user.getId());
        inOrder.verifyNoMoreInteractions();

        assertEquals((long) competition.getId(), model.getCompetitionId());
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(profile.getBusinessType(), model.getProfile().getBusinessType());
        assertEquals(profile.getSkillsAreas(), model.getProfile().getSkillsAreas());
        assertEquals(pageResource.getAllocatedApplications(), model.getAllocatedApplications());
        assertEquals(pageResource.getUnallocatedApplications(), model.getUnallocatedApplications());
        assertEquals(pageResource.getContent().size(), model.getRows().size());
    }

    @Test
    public void allocated() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withName("Competition x")
                .build();

        UserResource user = newUserResource()
                .withId(1L)
                .withFirstName("Kieran")
                .withLastName("Hester")
                .build();

        InnovationAreaResource innovationArea = newInnovationAreaResource()
                .withSector(1l)
                .withSectorName("Digital manufacturing")
                .build();

        ProfileResource profile = newProfileResource()
                .withInnovationAreas(singletonList(innovationArea))
                .withSkillsAreas("Skills")
                .withBusinessType(BusinessType.ACADEMIC)
                .build();

        AssessorProfileResource assessorProfile = newAssessorProfileResource()
                .withUser(user)
                .withProfile(profile)
                .build();

        InterviewApplicationPageResource pageResource = newInterviewApplicationPageResource()
                .withAllocatedApplications(1L)
                .withUnallocatedApplications(2L)
                .withContent(newInterviewApplicationResource()
                        .withId(1L, 2L)
                        .withLeadOrganisation("Lead 1", "Lead 2")
                        .withNumberOfAssessors(1L, 2L)
                        .build(2))
                .build();

        when(userRestServiceMock.retrieveUserById(user.getId())).thenReturn(restSuccess(user));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(assessorProfile));
        when(interviewAllocationRestService.getAllocatedApplications(competition.getId(), user.getId(), 0)).thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}/assessors/allocated-applications/{userId}", competition.getId(), user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview/allocated-applications"))
                .andReturn();

        InterviewAssessorApplicationsViewModel model = (InterviewAssessorApplicationsViewModel) result.getModelAndView().getModel().get("model");

        InOrder inOrder = inOrder(competitionRestService, userRestServiceMock, assessorRestService);
        inOrder.verify(userRestServiceMock).retrieveUserById(user.getId());
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(assessorRestService).getAssessorProfile(user.getId());
        inOrder.verifyNoMoreInteractions();

        assertEquals((long) competition.getId(), model.getCompetitionId());
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(profile.getBusinessType(), model.getProfile().getBusinessType());
        assertEquals(profile.getSkillsAreas(), model.getProfile().getSkillsAreas());
        assertEquals(pageResource.getAllocatedApplications(), model.getAllocatedApplications());
        assertEquals(pageResource.getUnallocatedApplications(), model.getUnallocatedApplications());
        assertEquals(pageResource.getContent().size(), model.getRows().size());
    }

    @Test
    public void testRemove() throws Exception {
        long competitionId = 2L;
        long userId = 3L;
        long idToRemove = 4L;
        InterviewAllocationSelectionForm selectionForm = new InterviewAllocationSelectionForm();
        selectionForm.getSelectedIds().add(idToRemove);
        selectionForm.setAllSelected(true);

        String cookieContent = JsonUtil.getSerializedObject(selectionForm);
        String cookieName = format("%s_comp_%s_%s", InterviewAllocationController.SELECTION_FORM, competitionId, userId);
        Cookie cookie = new Cookie(cookieName, getCompressedString(cookieContent));

        MvcResult result = mockMvc.perform(post("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{userId}", competitionId, userId)
            .param("remove", String.valueOf(idToRemove))
            .cookie(cookie))
            .andExpect(redirectedUrl(format("/assessment/interview/competition/%s/assessors/allocate-applications/%s", competitionId, userId)))
            .andReturn();

        Cookie resultCookie = result.getResponse().getCookie(cookieName);
        String resultContent = getDecompressedString(resultCookie.getValue());
        InterviewAllocationSelectionForm resultForm = JsonUtil.getObjectFromJson(resultContent, InterviewAllocationSelectionForm.class);

        assertTrue(resultForm.getSelectedIds().isEmpty());
        assertFalse(resultForm.getAllSelected());
    }

    @Test
    public void allocateApplications() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withName("Competition")
                .build();
        UserResource user = newUserResource()
                .withFirstName("Kieran")
                .withLastName("Hester")
                .build();
        List<Long> selectedApplicationIds = asList(1L);
        AssessorInvitesToSendResource assessorInvitesToSendResource = newAssessorInvitesToSendResource().withContent("content").build();
        List<InterviewApplicationResource> interviewApplicationResources = newInterviewApplicationResource().build(1);

        InterviewAllocationSelectionForm selectionForm = new InterviewAllocationSelectionForm();
        selectionForm.setSelectedIds(selectedApplicationIds);
        String cookieContent = JsonUtil.getSerializedObject(selectionForm);
        String cookieName = format("%s_comp_%s_%s", InterviewAllocationController.SELECTION_FORM, competition.getId(), user.getId());
        Cookie cookie = new Cookie(cookieName, getCompressedString(cookieContent));

        when(userRestServiceMock.retrieveUserById(user.getId())).thenReturn(restSuccess(user));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(interviewAllocationRestService.getUnallocatedApplicationsById(competition.getId(), selectedApplicationIds))
                .thenReturn(restSuccess(interviewApplicationResources));
        when(interviewAllocationRestService.getInviteToSend(competition.getId(), user.getId())).thenReturn(restSuccess(assessorInvitesToSendResource));
        when(competitionService.getById(competition.getId())).thenReturn(competition);


        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{userId}", competition.getId(), user.getId())
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview/allocate-applications"))
                .andReturn();

        InterviewAllocateApplicationsViewModel model = (InterviewAllocateApplicationsViewModel) result.getModelAndView().getModel().get("model");

        InterviewAllocateApplicationsViewModel expectedViewModel = new InterviewAllocateApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                user,
                assessorInvitesToSendResource.getContent(),
                interviewApplicationResources
        );

        assertEquals(expectedViewModel, model);

        InterviewAllocationNotifyForm form = (InterviewAllocationNotifyForm) result.getModelAndView().getModel().get("form");

        assertEquals(format("Applications for interview panel for '%s'", competition.getName()), form.getSubject());

        String originQuery = (String) result.getModelAndView().getModel().get("originQuery");
        assertEquals(format("?origin=INTERVIEW_PANEL_ALLOCATE&assessorId=%d", user.getId()), originQuery);

        InOrder inOrder = inOrder(userRestServiceMock, competitionRestService, interviewAllocationRestService, competitionService);
        inOrder.verify(userRestServiceMock).retrieveUserById(user.getId());
        inOrder.verify(interviewAllocationRestService).getUnallocatedApplicationsById(competition.getId(), selectedApplicationIds);
        inOrder.verify(interviewAllocationRestService).getInviteToSend(competition.getId(), user.getId());
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void notifyAssessor() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withName("Competition")
                .build();
        UserResource user = newUserResource()
                .withFirstName("Kieran")
                .withLastName("Hester")
                .build();
        List<Long> selectedApplicationIds = asList(1L);

        InterviewAllocationNotifyForm form = new InterviewAllocationNotifyForm();
        form.setSubject("subject");
        form.setContent("content");

        InterviewAllocationSelectionForm selectionForm = new InterviewAllocationSelectionForm();
        selectionForm.setSelectedIds(selectedApplicationIds);
        String cookieContent = JsonUtil.getSerializedObject(selectionForm);
        String cookieName = format("%s_comp_%s_%s", InterviewAllocationController.SELECTION_FORM, competition.getId(), user.getId());
        Cookie cookie = new Cookie(cookieName, getCompressedString(cookieContent));

        InterviewNotifyAllocationResource interviewNotifyAllocationResource = newInterviewNotifyAllocationResource()
                .withCompetitionId(competition.getId())
                .withAssessorId(user.getId())
                .withSubject(form.getSubject())
                .withContent(form.getContent())
                .withApplicationIds(selectionForm.getSelectedIds())
                .build();

        when(interviewAllocationRestService.notifyAllocations(interviewNotifyAllocationResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{userId}", competition.getId(), user.getId())
                .cookie(cookie)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("subject", form.getSubject())
                .param("content", form.getContent()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/interview/competition/%s/assessors/allocated-applications/%s", competition.getId(), user.getId())))
                .andReturn();

        verify(interviewAllocationRestService, only()).notifyAllocations(interviewNotifyAllocationResource);
    }
}