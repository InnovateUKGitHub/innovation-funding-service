package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.AbstractYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.DefaultProjectCostSection;
import org.innovateuk.ifs.application.viewmodel.section.StandardYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.JesYourProjectCostsSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Your project costs populator section view models.
 */
@Component
public class YourProjectCostsSectionPopulator extends AbstractSectionPopulator<AbstractYourProjectCostsSectionViewModel> {

    private SectionService sectionService;
    private FinanceViewHandlerProvider financeViewHandlerProvider;
    private FormInputViewModelGenerator formInputViewModelGenerator;

    public YourProjectCostsSectionPopulator(final ApplicationNavigationPopulator navigationPopulator,
                                            final SectionService sectionService,
                                            final FinanceViewHandlerProvider financeViewHandlerProvider,
                                            final FormInputViewModelGenerator formInputViewModelGenerator) {
        super(navigationPopulator);
        this.sectionService = sectionService;
        this.financeViewHandlerProvider = financeViewHandlerProvider;
        this.formInputViewModelGenerator = formInputViewModelGenerator;
    }

    @Override
    public void populateNoReturn(
            ApplicantSectionResource section,
            ApplicationForm form,
            AbstractYourProjectCostsSectionViewModel viewModel,
            Model model,
            BindingResult bindingResult,
            Boolean readOnly,
            Optional<Long> applicantOrganisationId
    ) {
        List<ApplicantQuestionResource> costQuestions =
                section.allQuestions()
                        .filter(question -> QuestionType.COST.equals(question.getQuestion().getType()))
                        .collect(Collectors.toList());
        financeViewHandlerProvider.getFinanceModelManager(section.getCompetition(),
                section.getCurrentApplicant().getOrganisation().getOrganisationType()
        )
                .addOrganisationFinanceDetails(
                        model,
                        section.getApplication().getId(),
                        simpleMap(costQuestions, ApplicantQuestionResource::getQuestion),
                        section.getCurrentUser().getId(),
                        form,
                        section.getCurrentApplicant().getOrganisation().getId()
                );

        viewModel.setCostQuestions(costQuestions);
        viewModel.setApplicantQuestion(section.getApplicantQuestions().get(0));
        List<Long> completedSectionIds = sectionService.getCompleted(
                section.getApplication().getId(),
                section.getCurrentApplicant().getOrganisation().getId()
        );
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));

        if (viewModel instanceof StandardYourProjectCostsSectionViewModel) {
            StandardYourProjectCostsSectionViewModel standardViewModel = (StandardYourProjectCostsSectionViewModel) viewModel;
            standardViewModel.setDefaultProjectCostSections(section.getApplicantChildrenSections().stream().map(
                    childSection -> {
                        DefaultProjectCostSection costSection = new DefaultProjectCostSection();
                        costSection.setApplicantResource(section);
                        costSection.setApplicantSection(childSection);
                        costSection.setCostViews(formInputViewModelGenerator.fromSection(
                                section,
                                childSection,
                                form,
                                readOnly
                        ));
                        return costSection;
                    }).collect(Collectors.toList()));
        } else if (viewModel instanceof JesYourProjectCostsSectionViewModel) {
            section.allQuestions().forEach(applicantQuestion ->
                applicantQuestion.getApplicantFormInputs().forEach(applicantFormInput -> {
                    if (applicantFormInput.getFormInput().getType().equals(FormInputType.FINANCE_UPLOAD)) {
                        JesYourProjectCostsSectionViewModel jesModel = (JesYourProjectCostsSectionViewModel) viewModel;
                        jesModel.setFinanceUploadFormInput(applicantFormInput.getFormInput());
                        jesModel.setFinanceUploadQuestion(applicantQuestion.getQuestion());
                    }
                })
            );
        }
    }

    @Override
    protected AbstractYourProjectCostsSectionViewModel createNew(
            ApplicantSectionResource section,
            ApplicationForm form,
            Boolean readOnly,
            Optional<Long> applicantOrganisationId,
            Boolean readOnlyAllApplicantApplicationFinances
    ) {
        List<Long> completedSectionIds = sectionService.getCompleted(
                section.getApplication().getId(),
                section.getCurrentApplicant().getOrganisation().getId()
        );
        boolean viewModelIsReadOnly = readOnly || completedSectionIds.contains(section.getSection().getId());
        if (section.getCompetition().showJesFinances(section.getCurrentApplicant().getOrganisation().getOrganisationType())) {
            return new JesYourProjectCostsSectionViewModel(
                    section,
                    Collections.emptyList(),
                    getNavigationViewModel(section),
                    viewModelIsReadOnly,
                    applicantOrganisationId,
                    readOnlyAllApplicantApplicationFinances
            );
        } else {
            return new StandardYourProjectCostsSectionViewModel(
                    section,
                    Collections.emptyList(),
                    getNavigationViewModel(section),
                    viewModelIsReadOnly,
                    applicantOrganisationId,
                    readOnlyAllApplicantApplicationFinances
            );
        }
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.PROJECT_COST_FINANCES;
    }
}

