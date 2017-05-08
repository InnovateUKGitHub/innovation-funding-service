package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.CapitalUsageCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class CapitalUsageCostPopulator extends AbstractCostPopulator<CapitalUsageCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.CAPITAL_USAGE;
    }

    @Override
    protected CapitalUsageCostViewModel createNew() {
        return new CapitalUsageCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, CapitalUsageCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {

    }
}
