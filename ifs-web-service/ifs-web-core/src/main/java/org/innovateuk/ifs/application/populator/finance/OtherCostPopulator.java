package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.OtherCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for other costs form inputs.
 */
@Component
public class OtherCostPopulator extends AbstractCostPopulator<OtherCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.OTHER_COSTS;
    }

    @Override
    protected OtherCostViewModel createNew() {
        return new OtherCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, OtherCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {

    }
}
