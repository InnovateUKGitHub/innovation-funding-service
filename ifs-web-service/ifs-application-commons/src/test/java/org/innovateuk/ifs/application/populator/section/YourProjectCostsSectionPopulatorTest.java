package org.innovateuk.ifs.application.populator.section;


import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.FinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.application.viewmodel.section.DefaultProjectCostSection;
import org.innovateuk.ifs.application.viewmodel.section.JesYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.StandardYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD_WITH_VAT;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.COLLABORATIVE;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YourProjectCostsSectionPopulatorTest {

    @InjectMocks
    private YourProjectCostsSectionPopulator yourProjectCostsSectionPopulator;

    @Mock
    private ApplicationNavigationPopulator navigationPopulator;

    @Mock
    private SectionService sectionService;

    @Mock
    private FinanceViewHandlerProvider financeViewHandlerProvider;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private MessageSource messageSource;

    private CompetitionResource competition;
    private OrganisationResource organisation;
    private QuestionResource costQuestion;
    private ApplicantSectionResource costSection;
    private List<ApplicantQuestionResource> costApplicantQuestions;
    private ApplicantSectionResource section;
    private ApplicationForm form;
    private Model model;
    private BindingResult bindingResult;
    private AbstractFormInputViewModel formInputViewModel;
    private FinanceModelManager financeModelManager;

    @Before
    public void setUp() {
        competition = newCompetitionResource().withCollaborationLevel(SINGLE).build();

        organisation = newOrganisationResource().build();

        costQuestion = newQuestionResource().withType(QuestionType.COST).build();

        costApplicantQuestions = newApplicantQuestionResource()
                .withQuestion(costQuestion)
                .build(1);

        costSection = newApplicantSectionResource()
                .withApplicantQuestions(costApplicantQuestions)
                .build();

        section = newApplicantSectionResource()
                .withCurrentApplicant(newApplicantResource().withOrganisation(organisation).build())
                .withCurrentUser(newUserResource().build())
                .withCompetition(competition)
                .withApplication(newApplicationResource().build())
                .withApplicantQuestions(newApplicantQuestionResource().withQuestion(newQuestionResource().withType(QuestionType.GENERAL).build()).withApplicantFormInputs(Collections.emptyList()).build(1))
                .withApplicantChildrenSections(asList(costSection))
                .withSection(newSectionResource().build())
                .build();

        form = mock(ApplicationForm.class);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
        formInputViewModel = mock(AbstractFormInputViewModel.class);
        financeModelManager = mock(FinanceModelManager.class);

        when(sectionService.getCompleted(section.getApplication().getId(),
                section.getCurrentApplicant().getOrganisation().getId())).thenReturn(Collections.emptyList());
        when(formInputViewModelGenerator.fromSection(section, costSection, form, false)).thenReturn(asList(formInputViewModel));
    }

    @Test
    public void populate_businessOrgType() {
        organisation.setOrganisationType(BUSINESS.getId());
        section.getCompetition().setApplicationFinanceType(STANDARD_WITH_VAT);

        when(financeViewHandlerProvider.getFinanceModelManager(BUSINESS.getId())).thenReturn(financeModelManager);
        when(messageSource.getMessage("ifs.question.yourProjectCosts.description", null, Locale.getDefault()))
                .thenReturn("Your project costs question description");

        StandardYourProjectCostsSectionViewModel viewModel =
                (StandardYourProjectCostsSectionViewModel) yourProjectCostsSectionPopulator.populate(section, form,
                        model, bindingResult, false, Optional.empty(), false);

        assertThat(viewModel.isSection()).isTrue();
        assertThat(viewModel.isComplete()).isFalse();
        assertThat(viewModel.getDefaultProjectCostSections().size()).isEqualTo(1);
        assertThat(viewModel.isIncludeVat()).isTrue();
        DefaultProjectCostSection costSectionViewModel = viewModel.getDefaultProjectCostSections().get(0);
        assertThat(costSectionViewModel.getApplicantResource()).isEqualTo(section);
        assertThat(costSectionViewModel.getApplicantSection()).isEqualTo(costSection);
        assertThat(costSectionViewModel.getCostViews()).isEqualTo(asList(formInputViewModel));
        assertThat(viewModel.getQuestion().getDescription()).isEqualTo("Your project costs question description");

        verify(messageSource, only()).getMessage("ifs.question.yourProjectCosts.description", null,
                Locale.getDefault());
        verify(financeModelManager).addOrganisationFinanceDetails(model, section.getApplication().getId(),
                asList(costQuestion), section.getCurrentUser().getId(), form,
                section.getCurrentApplicant().getOrganisation().getId());
    }

    @Test
    public void populate_collaborativeCompetition() {
        competition.setCollaborationLevel(COLLABORATIVE);
        organisation.setOrganisationType(BUSINESS.getId());
        section.getCompetition().setApplicationFinanceType(STANDARD_WITH_VAT);

        when(financeViewHandlerProvider.getFinanceModelManager(BUSINESS.getId())).thenReturn(financeModelManager);
        when(messageSource.getMessage("ifs.question.yourProjectCosts.collaborative.description", null,
                Locale.getDefault()))
                .thenReturn("Your project costs question collaborative description");

        StandardYourProjectCostsSectionViewModel viewModel =
                (StandardYourProjectCostsSectionViewModel) yourProjectCostsSectionPopulator.populate(section, form,
                        model, bindingResult, false, Optional.empty(), false);

        assertThat(viewModel.getQuestion().getDescription()).isEqualTo("Your project costs question " +
                "collaborative description");

        verify(messageSource, only()).getMessage("ifs.question.yourProjectCosts.collaborative.description", null,
                Locale.getDefault());
    }

    @Test
    public void populate_researchOrgType() {
        organisation.setOrganisationType(RESEARCH.getId());

        FormInputResource fileUpload = newFormInputResource().withType(FormInputType.FINANCE_UPLOAD).build();

        costApplicantQuestions.get(0).setApplicantFormInputs(newApplicantFormInputResource()
                .withFormInput(fileUpload)
                .build(1)
        );

        when(financeViewHandlerProvider.getFinanceModelManager(RESEARCH.getId())).thenReturn(financeModelManager);
        when(messageSource.getMessage("ifs.question.yourProjectCosts.description", null, Locale.getDefault()))
                .thenReturn("Your project costs question description");

        JesYourProjectCostsSectionViewModel viewModel =
                (JesYourProjectCostsSectionViewModel) yourProjectCostsSectionPopulator.populate(section, form, model,
                        bindingResult, true, Optional.empty(), true);

        assertThat(viewModel.isSection()).isTrue();
        assertThat(viewModel.isComplete()).isFalse();
        assertThat(viewModel.getFinanceUploadFormInput()).isEqualTo(fileUpload);
        assertThat(viewModel.getFinanceUploadQuestion()).isEqualTo(costQuestion);
        assertThat(viewModel.getQuestion().getDescription()).isEqualTo("Your project costs question description");

        verify(messageSource, only()).getMessage("ifs.question.yourProjectCosts.description", null,
                Locale.getDefault());
        verify(financeModelManager).addOrganisationFinanceDetails(model, section.getApplication().getId(),
                asList(costQuestion), section.getCurrentUser().getId(), form,
                section.getCurrentApplicant().getOrganisation().getId());
    }
}
