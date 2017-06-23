package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;

import java.util.List;
import java.util.Optional;

/**
 * Your project costs view model for jes finances.
 */
public class JesYourProjectCostsSectionViewModel extends AbstractYourProjectCostsSectionViewModel {
    private QuestionResource financeUploadQuestion;
    private FormInputResource financeUploadFormInput;

    public JesYourProjectCostsSectionViewModel(ApplicantSectionResource applicantResource,
                                               List<AbstractFormInputViewModel> formInputViewModels,
                                               NavigationViewModel navigationViewModel,
                                               boolean allReadOnly,
                                               Optional<Long> applicantOrganisationId,
                                               boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public String getFinanceView() {
        return "academic-finance";
    }

    public QuestionResource getFinanceUploadQuestion() {
        return financeUploadQuestion;
    }

    public void setFinanceUploadQuestion(QuestionResource financeUploadQuestion) {
        this.financeUploadQuestion = financeUploadQuestion;
    }

    public FormInputResource getFinanceUploadFormInput() {
        return financeUploadFormInput;
    }

    public void setFinanceUploadFormInput(FormInputResource financeUploadFormInput) {
        this.financeUploadFormInput = financeUploadFormInput;
    }
}

