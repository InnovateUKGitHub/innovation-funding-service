package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.OtherFundingCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for other funding form inputs.
 */
@Component
public class OtherFundingCostPopulator extends AbstractCostPopulator<OtherFundingCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.OTHER_FUNDING;
    }

    @Override
    protected OtherFundingCostViewModel createNew() {
        return new OtherFundingCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, OtherFundingCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {

    }
}
