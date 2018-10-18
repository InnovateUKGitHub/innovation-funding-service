package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.finance.OverheadCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for overheads cost form inputs.
 */
@Component
public class OverheadsCostPopulator extends AbstractCostPopulator<OverheadCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.OVERHEADS;
    }

    @Override
    protected OverheadCostViewModel createNew() {
        return new OverheadCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, OverheadCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {
        ApplicantSectionResource parentSection = (ApplicantSectionResource) resource;
        viewModel.setLabourQuestion(parentSection.allQuestions().filter(applicantQuestion -> applicantQuestion.getApplicantFormInputs().stream()
                .anyMatch(applicantFormInput -> applicantFormInput.getFormInput().getType().equals(FormInputType.LABOUR)))
                .findAny().map(ApplicantQuestionResource::getQuestion).orElse(null));
    }
}
