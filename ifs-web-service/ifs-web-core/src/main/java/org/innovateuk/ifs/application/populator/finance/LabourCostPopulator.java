package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.LabourCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class LabourCostPopulator extends AbstractCostPopulator<LabourCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.LABOUR;
    }

    @Override
    protected LabourCostViewModel createNew() {
        return new LabourCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, LabourCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {

    }
}
