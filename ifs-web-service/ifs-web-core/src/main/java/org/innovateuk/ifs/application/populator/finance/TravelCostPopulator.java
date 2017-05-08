package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.TravelCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class TravelCostPopulator extends AbstractCostPopulator<TravelCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.TRAVEL;
    }

    @Override
    protected TravelCostViewModel createNew() {
        return new TravelCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, TravelCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {

    }
}
