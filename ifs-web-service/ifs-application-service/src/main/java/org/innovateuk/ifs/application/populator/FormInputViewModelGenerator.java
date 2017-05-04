package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputPopulator;
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
 * Created by luke.harper on 03/05/2017.
 */
@Component
public class FormInputViewModelGenerator {

    private Map<FormInputType, FormInputPopulator<? extends AbstractApplicantResource, ? extends AbstractFormInputViewModel>> populators;

    private Map<FormInputType, FormInputPopulator<ApplicantSectionResource, ? extends AbstractFormInputViewModel>> sectionPopulators;

    @Autowired
    public void setPopulators(Collection<FormInputPopulator<? extends AbstractApplicantResource, ? extends AbstractFormInputViewModel>> populators) {
        this.populators = populators.stream().collect(Collectors.toMap(FormInputPopulator::type, Function.identity()));
    }

//    @Autowired
    public void setSectionPopulators(Collection<FormInputPopulator<ApplicantSectionResource, ? extends AbstractFormInputViewModel>> sectionPopulators) {
        this.sectionPopulators = sectionPopulators.stream().collect(Collectors.toMap(FormInputPopulator::type, Function.identity()));
    }

    public List<AbstractFormInputViewModel> fromQuestion(ApplicantQuestionResource question, ApplicationForm form) {
        List<AbstractFormInputViewModel> viewModels =  question.getApplicantFormInputs().stream()
                .map(applicantFormInputResource -> getPopulator(applicantFormInputResource.getFormInput().getType()).populate(question, question, applicantFormInputResource, applicantFormInputResource.responseForApplicant(question.getCurrentApplicant(), question)))
                .collect(Collectors.toList());
        viewModels.forEach(viewModel -> getPopulator(viewModel.getFormInput().getType()).addToForm(form, viewModel));
        return viewModels;
    }

    @SuppressWarnings("unchecked")
    public <R extends AbstractApplicantResource, M extends AbstractFormInputViewModel> FormInputPopulator<R, M> getPopulator(FormInputType formInputType) {
       return (FormInputPopulator<R, M>) populators.get(formInputType);
    }

    public List<AbstractFormInputViewModel> fromSection(ApplicantSectionResource sectionResource) {
//        return sectionResource.getApplicantQuestions().stream()
//                .map(questionResource -> questionResource.getApplicantFormInputs().stream()
//                        .map(applicantFormInputResource -> populators.get(applicantFormInputResource.getFormInput().getType()).populate(sectionResource, questionResource, applicantFormInputResource, applicantFormInputResource.responseForApplicant(sectionResource.getCurrentApplicant()))).collect(Collectors.toList())
//        ).flatMap(List::stream)
//        .collect(Collectors.toList());
        return null;
    }


}
