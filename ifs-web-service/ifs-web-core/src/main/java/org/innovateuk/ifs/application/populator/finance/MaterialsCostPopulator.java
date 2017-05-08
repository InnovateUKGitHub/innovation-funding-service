package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.MaterialsCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class MaterialsCostPopulator extends AbstractCostPopulator<MaterialsCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.MATERIALS;
    }

    @Override
    protected MaterialsCostViewModel createNew() {
        return new MaterialsCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, MaterialsCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {

    }
}
