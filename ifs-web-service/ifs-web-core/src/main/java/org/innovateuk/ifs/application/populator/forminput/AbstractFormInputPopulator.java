package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

/**
 * Created by luke.harper on 03/05/2017.
 */
public abstract class AbstractFormInputPopulator<M extends AbstractFormInputViewModel> implements FormInputPopulator<M> {

    @Override
    public M populate(AbstractApplicantResource applicantResource,
                      ApplicantSectionResource applicantSection,
                      ApplicantQuestionResource applicantQuestion,
                     ApplicantFormInputResource applicantFormInput,
                     ApplicantFormInputResponseResource applicantResponse) {

        M viewModel = createNew();
        viewModel.setApplicantSection(applicantSection);
        viewModel.setApplicantQuestion(applicantQuestion);
        viewModel.setApplicantFormInput(applicantFormInput);
        viewModel.setApplicantResponse(applicantResponse);
        viewModel.setCurrentApplicant(applicantResource.getCurrentApplicant());
        viewModel.setComplete(isComplete(applicantQuestion, applicantResource.getCurrentApplicant()));
        viewModel.setReadonly(viewModel.isComplete());
        populate(applicantResource, viewModel);
        return viewModel;
    }

    @Override
    public void addToForm(ApplicationForm form, M viewModel) {
        if (viewModel.getHasResponse()) {
            form.addFormInput(viewModel.getFormInput().getId().toString(), viewModel.getResponse().getValue());
        }
    }


    private boolean isComplete(ApplicantQuestionResource applicantQuestion, ApplicantResource currentApplicant) {
        return applicantQuestion.isCompleteByApplicant(currentApplicant);
    }

    protected abstract void populate(AbstractApplicantResource resource, M viewModel);

    protected abstract M createNew();
}
