package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.viewmodel.finance.CapitalUsageCostViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for capital usage form inputs.
 */
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

}
