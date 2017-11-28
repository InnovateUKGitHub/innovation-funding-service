package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Component to generate form input view models for application sections and questions.
 */
@Component
public class FormInputViewModelGenerator {

    private Map<FormInputType, FormInputPopulator<? extends AbstractFormInputViewModel>> populators;

    @Autowired
    public void setPopulators(Collection<FormInputPopulator<? extends AbstractFormInputViewModel>> autowiredPopulators) {
        this.populators = autowiredPopulators.stream().collect(Collectors.toMap(FormInputPopulator::type, Function.identity()));
    }

    public List<AbstractFormInputViewModel> fromQuestion(ApplicantQuestionResource question, ApplicationForm form) {
        List<AbstractFormInputViewModel> viewModels =  question.getApplicantFormInputs().stream()
                .filter(applicantFormInput -> applicantFormInput.getFormInput().getType().isDisplayableQuestionType())
                .map(applicantFormInput -> getPopulator(applicantFormInput.getFormInput().getType()).populate(question, null, question, applicantFormInput, applicantFormInput.responseForApplicant(question.getCurrentApplicant(), question)))
                .collect(Collectors.toList());
        viewModels.forEach(viewModel -> getPopulator(viewModel.getFormInput().getType()).addToForm(form, viewModel));
        return viewModels;
    }

    @SuppressWarnings("unchecked")
    public <M extends AbstractFormInputViewModel> FormInputPopulator<M> getPopulator(FormInputType formInputType) {
       return (FormInputPopulator<M>) populators.get(formInputType);
    }

    public List<AbstractFormInputViewModel> fromSection(ApplicantSectionResource applicantResource, ApplicantSectionResource childSection, ApplicationForm form, Boolean readOnly) {
        List<AbstractFormInputViewModel> viewModels =  childSection.getApplicantQuestions().stream()
                .map(applicantQuestion -> applicantQuestion.getApplicantFormInputs().stream()
                        .map(applicantFormInputResource ->
                                populateApplicationFormInput(applicantFormInputResource, applicantResource, childSection, applicantQuestion, readOnly))
                .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        viewModels.forEach(viewModel -> getPopulator(viewModel.getFormInput().getType()).addToForm(form, viewModel));

        return viewModels;
    }

    private AbstractFormInputViewModel populateApplicationFormInput(ApplicantFormInputResource applicantFormInputResource, ApplicantSectionResource applicantResource, ApplicantSectionResource childSection, ApplicantQuestionResource applicantQuestion, Boolean readOnly) {
        return getPopulator(applicantFormInputResource.getFormInput().getType())
                        .populate(applicantResource,
                                childSection,
                                applicantQuestion,
                                applicantFormInputResource,
                                applicantFormInputResource.responseForApplicant(applicantResource.getCurrentApplicant(), applicantQuestion),
                                readOnly);
    }
}
