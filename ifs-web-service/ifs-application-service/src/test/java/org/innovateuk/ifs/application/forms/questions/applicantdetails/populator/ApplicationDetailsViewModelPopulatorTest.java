package org.innovateuk.ifs.application.forms.questions.applicantdetails.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.populator.ApplicationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.forminput.ApplicationDetailsPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationDetailsViewModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationDetailsViewModelPopulator populator;
    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;
    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;
    @Mock
    private QuestionService questionService;
    @Mock
    private ApplicationDetailsPopulator applicationDetailsPopulator;

    @Test
    public void populate() {

        ApplicantQuestionResource question =  newApplicantQuestionResource()
                .withQuestion(newQuestionResource().build())
                .withApplicantFormInputs(singletonList(newApplicantFormInputResource().withApplicantResponses(emptyList()).build()))
                .withApplication(newApplicationResource().withApplicationState(ApplicationState.OPENED).build())
                .withCurrentApplicant(newApplicantResource()
                        .withOrganisation(newOrganisationResource().build())
                        .withProcessRole(newProcessRoleResource().withRole(LEADAPPLICANT).build())
                        .build())
                .withCurrentUser(newUserResource().build())
                .build();
        ApplicationForm form = mock(ApplicationForm.class);
        ApplicationDetailsInputViewModel applicationDetailsInputViewModel = mock(ApplicationDetailsInputViewModel.class);
        when(applicationDetailsInputViewModel.isReadonly()).thenReturn(true);
        QuestionStatusResource questionStatusResource = newQuestionStatusResource().build();
        List<QuestionStatusResource> notifications = newQuestionStatusResource().build(1);
        NavigationViewModel navigationViewModel = new NavigationViewModel();
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();

        when(applicationDetailsPopulator.populate(
                any(AbstractApplicantResource.class),
                isNull(),
                any(ApplicantQuestionResource.class),
                any(ApplicantFormInputResource.class),
                isNull())).thenReturn(applicationDetailsInputViewModel);

        when(formInputViewModelGenerator.fromQuestion(question, form)).thenReturn(singletonList(applicationDetailsInputViewModel));
        when(applicationNavigationPopulator.addNavigation(question.getQuestion(), question.getApplication().getId())).thenReturn(navigationViewModel);
        when(questionService.getByQuestionIdAndApplicationIdAndOrganisationId(question.getQuestion().getId(), question.getApplication().getId(), question.getCurrentApplicant().getOrganisation().getId())).thenReturn(questionStatusResource);
        when(questionService.getNotificationsForUser(singletonList(questionStatusResource), question.getCurrentUser().getId())).thenReturn(notifications);

        ApplicationDetailsViewModel viewModel = populator.populate(question, competitionResource);

        assertThat(viewModel.isAllReadOnly(), equalTo(true));
        assertThat(viewModel.getCurrentApplicant(), equalTo(question.getCurrentApplicant()));
        assertThat(viewModel.isQuestion(), equalTo(true));
        assertThat(viewModel.isSection(), equalTo(false));
        assertThat(viewModel.getNavigation(), equalTo(navigationViewModel));
        assertThat(viewModel.isLeadApplicant(), equalTo(true));

        verify(questionService).removeNotifications(notifications);
    }

}
