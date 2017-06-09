package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * Interface for populating a form input view model.
 */
public interface FormInputPopulator<M extends AbstractFormInputViewModel> {

    M populate(AbstractApplicantResource applicantResource,
                      ApplicantSectionResource applicantSection,
                      ApplicantQuestionResource applicantQuestion,
                      ApplicantFormInputResource applicantFormInput,
                      ApplicantFormInputResponseResource applicantResponse);

    M populate(AbstractApplicantResource applicantResource,
               ApplicantSectionResource applicantSection,
               ApplicantQuestionResource applicantQuestion,
               ApplicantFormInputResource applicantFormInput,
               ApplicantFormInputResponseResource applicantResponse,
               boolean readOnly);

    void addToForm(ApplicationForm form, M viewModel);

    FormInputType type();
}
