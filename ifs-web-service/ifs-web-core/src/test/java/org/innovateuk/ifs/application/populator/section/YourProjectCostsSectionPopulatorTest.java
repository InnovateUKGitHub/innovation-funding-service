package org.innovateuk.ifs.application.populator.section;


import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.FinanceModelManager;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.application.viewmodel.section.DefaultProjectCostSection;
import org.innovateuk.ifs.application.viewmodel.section.DefaultYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.JesYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link YourProjectCostsSectionPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class YourProjectCostsSectionPopulatorTest {

    @InjectMocks
    private YourProjectCostsSectionPopulator yourProjectCostsSectionPopulator;

    @Mock
    private ApplicationNavigationPopulator navigationPopulator;

    @Mock
    private SectionService sectionService;

    @Mock
    private FinanceHandler financeHandler;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;


    @Test
    public void testPopulateBusiness() {
        QuestionResource costQuestion = newQuestionResource().withType(QuestionType.COST).build();
        ApplicantSectionResource costSection = newApplicantSectionResource()
                .withApplicantQuestions(
                        newApplicantQuestionResource().withQuestion(costQuestion).build(1)
                ).build();

        ApplicantSectionResource section = newApplicantSectionResource()
                .withCurrentApplicant(newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build())
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().build())
                .withApplication(newApplicationResource().build())
                .withApplicantQuestions(newApplicantQuestionResource().withQuestion(newQuestionResource().withType(QuestionType.GENERAL).build()).build(1))
                .withApplicantChildrenSections(asList(costSection))
                .withSection(newSectionResource().build())
                .build();
        ApplicationForm form = mock(ApplicationForm.class);
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        AbstractFormInputViewModel formInputViewModel = mock(AbstractFormInputViewModel.class);
        FinanceModelManager financeModelManager = mock(FinanceModelManager.class);

        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(Collections.emptyList());
        when(formInputViewModelGenerator.fromSection(section, costSection, form, false)).thenReturn(asList(formInputViewModel));
        when(financeHandler.getFinanceModelManager(section.getCurrentApplicant().getOrganisation().getOrganisationType())).thenReturn(financeModelManager);
        DefaultYourProjectCostsSectionViewModel viewModel = (DefaultYourProjectCostsSectionViewModel) yourProjectCostsSectionPopulator.populate(section, form, model, bindingResult, false, Optional.empty(), false);

        assertThat(viewModel.isSection(), equalTo(true));
        assertThat(viewModel.isComplete(), equalTo(false));
        assertThat(viewModel.getDefaultProjectCostSections().size(), equalTo(1));
        DefaultProjectCostSection costSectionViewModel = viewModel.getDefaultProjectCostSections().get(0);
        assertThat(costSectionViewModel.getApplicantResource(), equalTo(section));
        assertThat(costSectionViewModel.getApplicantSection(), equalTo(costSection));
        assertThat(costSectionViewModel.getCostViews(), equalTo(asList(formInputViewModel)));


        verify(financeModelManager).addOrganisationFinanceDetails(model, section.getApplication().getId(), asList(costQuestion), section.getCurrentUser().getId(), form, section.getCurrentApplicant().getOrganisation().getId());
    }

    @Test
    public void testPopulateResearch() {
        QuestionResource costQuestion = newQuestionResource().withType(QuestionType.COST).build();
        FormInputResource fileUpload = newFormInputResource().withType(FormInputType.FINANCE_UPLOAD).build();
        ApplicantQuestionResource costApplicantQuestion = newApplicantQuestionResource()
                .withQuestion(costQuestion)
                .withApplicantFormInputs(
                        newApplicantFormInputResource()
                                .withFormInput(fileUpload)
                                .build(1)
                )
                .build();

        ApplicantSectionResource costSection = newApplicantSectionResource()
                .withApplicantQuestions(asList(costApplicantQuestion))
                .build();

        ApplicantSectionResource section = newApplicantSectionResource()
                .withCurrentApplicant(newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build()).build())
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().build())
                .withApplication(newApplicationResource().build())
                .withApplicantQuestions(newApplicantQuestionResource().withQuestion(newQuestionResource().withType(QuestionType.GENERAL).build()).withApplicantFormInputs(Collections.emptyList()).build(1))
                .withApplicantChildrenSections(asList(costSection))
                .withSection(newSectionResource().build())
                .build();
        ApplicationForm form = mock(ApplicationForm.class);
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        AbstractFormInputViewModel formInputViewModel = mock(AbstractFormInputViewModel.class);
        FinanceModelManager financeModelManager = mock(FinanceModelManager.class);

        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(Collections.emptyList());
        when(formInputViewModelGenerator.fromSection(section, costSection, form, false)).thenReturn(asList(formInputViewModel));
        when(financeHandler.getFinanceModelManager(section.getCurrentApplicant().getOrganisation().getOrganisationType())).thenReturn(financeModelManager);
        JesYourProjectCostsSectionViewModel viewModel = (JesYourProjectCostsSectionViewModel) yourProjectCostsSectionPopulator.populate(section, form, model, bindingResult, true, Optional.empty(), true);

        assertThat(viewModel.isSection(), equalTo(true));
        assertThat(viewModel.isComplete(), equalTo(false));
        assertThat(viewModel.getFinanceUploadFormInput(), equalTo(fileUpload));
        assertThat(viewModel.getFinanceUploadQuestion(), equalTo(costQuestion));

        verify(financeModelManager).addOrganisationFinanceDetails(model, section.getApplication().getId(), asList(costQuestion), section.getCurrentUser().getId(), form, section.getCurrentApplicant().getOrganisation().getId());
    }
}
