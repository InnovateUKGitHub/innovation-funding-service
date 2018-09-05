package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for organisation size form input.
 */
public class OrganisationSizeViewModel extends AbstractFormInputViewModel {

    private boolean organisationSizeAlert;
    private OrganisationSize organisationFinanceSize;

    @Override
    protected FormInputType formInputType() {
        return FormInputType.ORGANISATION_SIZE;
    }

    public boolean isOrganisationSizeAlert() {
        return organisationSizeAlert;
    }

    public void setOrganisationSizeAlert(boolean organisationSizeAlert) {
        this.organisationSizeAlert = organisationSizeAlert;
    }

    public OrganisationSize getOrganisationFinanceSize() {
        return organisationFinanceSize;
    }

    public void setOrganisationFinanceSize(OrganisationSize organisationFinanceSize) {
        this.organisationFinanceSize = organisationFinanceSize;
    }
}
