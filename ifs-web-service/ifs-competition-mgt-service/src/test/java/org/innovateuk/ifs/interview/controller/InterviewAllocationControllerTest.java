package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.form.InterviewAllocationSelectionForm;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.management.model.InterviewAcceptedAssessorsModelPopulator;
import org.innovateuk.ifs.management.model.UnallocatedInterviewApplicationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.InterviewAcceptedAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAcceptedAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InterviewAllocationControllerTest extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Spy
    @InjectMocks
    private InterviewAcceptedAssessorsModelPopulator interviewAcceptedAssessorsModelPopulator;

    @Spy
    @InjectMocks
    private UnallocatedInterviewApplicationsModelPopulator interviewApplicationsModelPopulator;

    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
    }

    @Before
    public void setUp() {
        super.setUp();
        setupCookieUtil();
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

        InnovationAreaResource innovationArea = newInnovationAreaResource()
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
    public void testRemove() throws Exception {
        long competitionId = 2L;
        long userId = 3L;
        long idToRemove = 4L;
        InterviewAllocationSelectionForm selectionForm = new InterviewAllocationSelectionForm();
        selectionForm.getSelectedIds().add(idToRemove);

        String cookieContent = JsonUtil.getSerializedObject(selectionForm);
        String cookieName = String.format("%s_comp_%s_%s", InterviewAllocationController.SELECTION_FORM, competitionId, userId);
        Cookie cookie = new Cookie(cookieName, getCompressedString(cookieContent));

        mockMvc.perform(post("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{userId}", competitionId, userId)
            .param("remove", String.valueOf(idToRemove))
            .cookie(cookie))
            .andExpect(redirectedUrl(String.format("/assessment/interview/competition/%s/assessors/allocate-applications/%s", competitionId, userId)))
            .andReturn();
    }
}