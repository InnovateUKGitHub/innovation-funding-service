package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

/**
 * Abstract class for populating a form input view model.
 */
public abstract class AbstractFormInputPopulator<M extends AbstractFormInputViewModel> implements FormInputPopulator<M> {

    @Override
    public M populate(AbstractApplicantResource applicantResource,
                      ApplicantSectionResource applicantSection,
                      ApplicantQuestionResource applicantQuestion,
                      ApplicantFormInputResource applicantFormInput,
                      ApplicantFormInputResponseResource applicantResponse) {
        return populate(applicantResource,
                        applicantSection,
                        applicantQuestion,
                        applicantFormInput,
                        applicantResponse,
                        false);
    }

    @Override
    public M populate(AbstractApplicantResource applicantResource,
                      ApplicantSectionResource applicantSection,
                      ApplicantQuestionResource applicantQuestion,
                      ApplicantFormInputResource applicantFormInput,
                      ApplicantFormInputResponseResource applicantResponse,
                      boolean readOnly) {

        M viewModel = createNew();
        viewModel.setApplicantSection(applicantSection);
        viewModel.setApplicantQuestion(applicantQuestion);
        viewModel.setApplicantFormInput(applicantFormInput);
        viewModel.setApplicantResponse(applicantResponse);
        viewModel.setCurrentApplicant(applicantResource.getCurrentApplicant());
        viewModel.setComplete(isComplete(applicantQuestion, applicantResource.getCurrentApplicant()));
        viewModel.setClosed(!applicantResource.getCompetition().isOpen() || !applicantResource.getApplication().isOpen());
        viewModel.setReadonly(viewModel.isComplete() || viewModel.isClosed() || readOnly);

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

    protected void populate(AbstractApplicantResource resource, M viewModel) {
        //Can be overridden by subclass.
    }

    protected abstract M createNew();
}
