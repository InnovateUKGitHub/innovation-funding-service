package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResponseResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

/**
 * Created by luke.harper on 03/05/2017.
 */
public abstract class AbstractFormInputPopulator<R extends AbstractApplicantResource, M extends AbstractFormInputViewModel> implements FormInputPopulator<R, M> {

    @Override
    public M populate(R resource,
                     ApplicantQuestionResource applicantQuestion,
                     ApplicantFormInputResource applicantFormInput,
                     ApplicantFormInputResponseResource applicantResponse) {

        M viewModel = createNew();
        viewModel.setApplicantQuestion(applicantQuestion);
        viewModel.setApplicantFormInput(applicantFormInput);
        viewModel.setApplicantResponse(applicantResponse);
        viewModel.setCurrentApplicant(resource.getCurrentApplicant());
        viewModel.setComplete(isComplete(resource, applicantQuestion));
        viewModel.setReadonly(viewModel.isComplete());
        populate(resource, viewModel);
        return viewModel;
    }

    @Override
    public void addToForm(ApplicationForm form, M viewModel) {
        form.addFormInput(viewModel.getFormInput().getId().toString(), viewModel.getResponse().getValue());
    }


    private boolean isComplete(R resource, ApplicantQuestionResource applicantQuestion) {
        return applicantQuestion.isCompleteByApplicant(resource.getCurrentApplicant());
    }

    protected abstract void populate(R resource, M viewModel);

    protected abstract M createNew();
}
