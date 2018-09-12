package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.viewmodel.finance.SubcontractingCostViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for subcontracting costs form inputs.
 */
@Component
public class SubcontractingCostPopulator extends AbstractCostPopulator<SubcontractingCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.SUBCONTRACTING;
    }

    @Override
    protected SubcontractingCostViewModel createNew() {
        return new SubcontractingCostViewModel();
    }

}
