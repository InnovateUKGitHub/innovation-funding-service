package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * Created by luke.harper on 03/05/2017.
 */
public interface FormInputPopulator<M extends AbstractFormInputViewModel> {

    M populate(AbstractApplicantResource applicantResource,
                      ApplicantSectionResource applicantSection,
                      ApplicantQuestionResource applicantQuestion,
                      ApplicantFormInputResource applicantFormInput,
                      ApplicantFormInputResponseResource applicantResponse);


    void addToForm(ApplicationForm form, M viewModel);

    FormInputType type();
}
