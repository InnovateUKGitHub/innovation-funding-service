package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.management.model.InterviewAcceptedAssessorsModelPopulator;
import org.innovateuk.ifs.management.model.InterviewApplicationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.InterviewAcceptedAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAcceptedAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InterviewAllocationControllerTest extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Spy
    @InjectMocks
    private InterviewAcceptedAssessorsModelPopulator interviewAcceptedAssessorsModelPopulator;

    @Spy
    @InjectMocks
    private InterviewApplicationsModelPopulator interviewApplicationsModelPopulator;

    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
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
                .andExpect(view().name("competition/interview-accepted-assessors"))
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

        when(userRestServiceMock.retrieveUserById(user.getId())).thenReturn(restSuccess(user));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(assessorProfile));

        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{userId}", competition.getId(), user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/interview-applications"))
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
    }
}