package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;

/**
 * Created by luke.harper on 08/05/2017.
 */
public class OrganisationSizeViewModel extends AbstractFormInputViewModel {

    private boolean organisationSizeAlert;
    private List<OrganisationSizeResource> organisationSizes;
    private Long organisationFinanceSize;

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

    public List<OrganisationSizeResource> getOrganisationSizes() {
        return organisationSizes;
    }

    public void setOrganisationSizes(List<OrganisationSizeResource> organisationSizes) {
        this.organisationSizes = organisationSizes;
    }

    public Long getOrganisationFinanceSize() {
        return organisationFinanceSize;
    }

    public void setOrganisationFinanceSize(Long organisationFinanceSize) {
        this.organisationFinanceSize = organisationFinanceSize;
    }
}
