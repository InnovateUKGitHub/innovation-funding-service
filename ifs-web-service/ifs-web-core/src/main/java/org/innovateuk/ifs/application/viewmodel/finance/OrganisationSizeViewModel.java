package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.Set;

public class OrganisationSizeViewModel extends AbstractFormInputViewModel {

    private boolean organisationSizeAlert; //from other view model.
    private Set<OrganisationSizeResource> organisationSizes;
    private Long organisationFinanceSize;

    @Override
    protected FormInputType formInputType() {
        return FormInputType.ORGANISATION_SIZE;
    }

}
