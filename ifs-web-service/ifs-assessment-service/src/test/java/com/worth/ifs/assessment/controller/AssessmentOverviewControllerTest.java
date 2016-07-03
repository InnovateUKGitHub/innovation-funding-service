package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.AppendixResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder;
import com.worth.ifs.assessment.builder.AssessmentResourceBuilder;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.builders.FileEntryResourceBuilder;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.form.builder.FormInputResourceBuilder;
import com.worth.ifs.form.builder.FormInputResponseResourceBuilder;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputRestService;
import com.worth.ifs.user.builder.ProcessRoleResourceBuilder;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentOverviewControllerTest  extends BaseControllerMockMVCTest<AssessmentOverviewController> {

    @InjectMocks
    private AssessmentOverviewController assessmentOverviewController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessmentFeedbackService assessmentFeedbackService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Spy
    @InjectMocks
    private AssessmentOverviewModelPopulator applicationOverviewModelPopulator;

    @Override
    protected AssessmentOverviewController supplyControllerUnderTest() {
        return new AssessmentOverviewController();
    }

    @Before
    public void setUp(){
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(Optional.ofNullable(organisations.get(0)));
    }

    @Test
    public void testAssessmentDetails() throws Exception {
        AssessmentResource assessment = AssessmentResourceBuilder.newAssessmentResource().withId(1L).withProcessRole(0L).build();
        ProcessRoleResource processRole = ProcessRoleResourceBuilder.newProcessRoleResource().withApplicationId(1L).build();
        processRole.setId(0L);
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(1L)
                .withAssessmentStartDate(LocalDateTime.now().minusDays(2))
                .withAssessmentEndDate(LocalDateTime.now().plusDays(4))
                .build();
        AssessmentFeedbackResource assessmentFeedback = AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource().withId(1L).withQuestion(1L).withAssessment(1L).build();
        List<AssessmentFeedbackResource> assessmentFeedbackList = new ArrayList<>();
        assessmentFeedbackList.add(assessmentFeedback);

        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L,2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(competitionService.getById(app.getCompetition())).thenReturn(competition);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(processRoleService.getById(assessment.getProcessRole())).thenReturn(settable(processRole));
        when(assessmentFeedbackService.getAllAssessmentFeedback(assessment.getId())).thenReturn(assessmentFeedbackList);
        Map<Long,AssessmentFeedbackResource> feedbackMap = new HashMap<>();
        feedbackMap.put(1L,assessmentFeedback);

        FileEntryResource fileEntry = FileEntryResourceBuilder.newFileEntryResource().build();
        FormInputResponseResource formInputResponse = FormInputResponseResourceBuilder.newFormInputResponseResource().withFormInputs(1L).withFileEntry(fileEntry).build();
        List<FormInputResponseResource> responses = new ArrayList<>();
        responses.add(formInputResponse);
        FormInputResource formInput = FormInputResourceBuilder.newFormInputResource().withId(1L).build();

        when(formInputResponseService.getByApplication(app.getId())).thenReturn(responses);
        when(formInputRestService.getById(formInputResponse.getFormInput())).thenReturn(restSuccess(formInput));
        when(fileEntryRestService.findOne(formInputResponse.getFileEntry())).thenReturn(restSuccess(fileEntry));

        List<AppendixResource> appendices = new ArrayList<>();
        AppendixResource appendix = new AppendixResource(app.getId(), formInput.getId(), "test", fileEntry);
        appendices.add(appendix);

        mockMvc.perform(get("/" + assessment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-application-overview"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("questionFeedback",feedbackMap))
                .andExpect(model().attribute("appendices",appendices))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())));
    }
}
