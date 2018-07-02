package org.innovateuk.ifs.assessment.review.controller.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentReviewApplicationSummaryModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator populator;

    @Mock
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Test
    public void testPopulateModel() {

        UserResource userResource = newUserResource()
                .withId(1L)
                .build();

        ApplicationResource applicationResource = newApplicationResource()
                .withId(1L)
                .withCompetition(2L)
                .build();

        List<AssessmentResource> assessmentResources = newAssessmentResource()
                .build(1);

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(applicationResource.getCompetition())
                .build();

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplication(applicationResource);

        List<FormInputResponseResource> formInputResponseResources = newFormInputResponseResource()
                .withValue("Applicant response")
                .build(1);

        List<ProcessRoleResource> userApplicationRoles = newProcessRoleResource()
                .withApplication(applicationResource.getId())
                .withUser(userResource)
                .withRole(Role.ASSESSOR)
                .build(1);

        List<AssessorFormInputResponseResource> assessorFormInputResponseResources = newAssessorFormInputResponseResource()
                .withAssessment(assessmentResources.get(0).getId())
                .build(1);

        List<FormInputResource> formInputResources = newFormInputResource()
                .build(2);

        Model model = mock(Model.class);

        Map<Long, FormInputResponseResource> mappedResponses = mock(Map.class);

        when(formInputResponseRestService.getResponsesByApplicationId(applicationResource.getId())).thenReturn(restSuccess(formInputResponseResources));
        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);
        when(formInputResponseService.mapFormInputResponsesToFormInput(formInputResponseResources)).thenReturn(mappedResponses);
        when(processRoleService.findProcessRolesByApplicationId(applicationResource.getId())).thenReturn(userApplicationRoles);
        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponsesForPanel(applicationResource.getId())).thenReturn(restSuccess(assessorFormInputResponseResources));
        when(assessmentRestService.getByUserAndApplication(userResource.getId(), applicationResource.getId())).thenReturn(restSuccess(assessmentResources));
        when(formInputRestService.getById(nullable(Long.class))).thenReturn(restSuccess(formInputResources.get(0)));
        when(formInputRestService.getById(nullable(Long.class))).thenReturn(restSuccess(formInputResources.get(1)));

        populator.populateModel(model, applicationForm, userResource, applicationResource.getId());

        verify(model).addAttribute("responses", mappedResponses);
        verify(model).addAttribute("feedback", emptyList());
        verify(model).addAttribute("score", emptyList());
        verify(model).addAttribute("feedbackSummary", assessmentResources);
    }
}
