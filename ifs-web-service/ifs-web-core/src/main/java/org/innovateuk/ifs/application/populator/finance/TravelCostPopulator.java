package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.viewmodel.finance.TravelCostViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for travel costs form inputs.
 */
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

}
