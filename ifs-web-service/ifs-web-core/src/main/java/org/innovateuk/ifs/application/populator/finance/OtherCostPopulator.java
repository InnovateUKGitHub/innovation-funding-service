package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.viewmodel.finance.OtherCostViewModel;
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

}
