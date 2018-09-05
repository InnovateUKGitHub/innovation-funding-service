package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.viewmodel.finance.LabourCostViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for labour form inputs.
 */
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

}
