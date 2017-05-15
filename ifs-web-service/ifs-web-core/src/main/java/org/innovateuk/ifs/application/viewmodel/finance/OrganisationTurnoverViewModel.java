package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for organisation turnover form input.
 */
public class OrganisationTurnoverViewModel extends AbstractFormInputViewModel {
    @Override
    protected FormInputType formInputType() {
        return FormInputType.ORGANISATION_TURNOVER;
    }
}
