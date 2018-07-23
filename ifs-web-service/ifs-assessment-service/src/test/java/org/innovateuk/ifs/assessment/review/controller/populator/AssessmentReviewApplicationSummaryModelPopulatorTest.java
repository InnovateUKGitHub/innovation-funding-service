package org.innovateuk.ifs.assessment.review.controller.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.common.populator.SummaryViewModelFragmentPopulator;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentReviewApplicationSummaryModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator populator;

    @Mock
    private SummaryViewModelFragmentPopulator summaryViewModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;

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

        SummaryViewModel summary = mock(SummaryViewModel.class);

        when(competitionRestService.getCompetitionById(applicationResource.getCompetition())).thenReturn(restSuccess(competitionResource));
        when(processRoleService.findProcessRolesByApplicationId(applicationResource.getId())).thenReturn(userApplicationRoles);
        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponsesForPanel(applicationResource.getId())).thenReturn(restSuccess(assessorFormInputResponseResources));
        when(formInputRestService.getById(nullable(Long.class))).thenReturn(restSuccess(formInputResources.get(0)));
        when(formInputRestService.getById(nullable(Long.class))).thenReturn(restSuccess(formInputResources.get(1)));
        when(summaryViewModelPopulator.populate(applicationResource.getId(), userResource, applicationForm)).thenReturn(summary);
        when(summary.getCurrentApplication()).thenReturn(applicationResource);
        when(summary.getFeedbackSummary()).thenReturn(assessmentResources);

        AssessmentReviewApplicationSummaryViewModel viewModel = populator.populateModel(applicationForm, userResource, applicationResource.getId());

        assertEquals(viewModel.getFeedbackViewModel().getFeedback(), emptyList());
        assertEquals(viewModel.getFeedbackViewModel().getScore(), emptyList());
        assertEquals(viewModel.getCompetition(), competitionResource);
        assertEquals(viewModel.getSummaryViewModel(), summary);
    }
}
