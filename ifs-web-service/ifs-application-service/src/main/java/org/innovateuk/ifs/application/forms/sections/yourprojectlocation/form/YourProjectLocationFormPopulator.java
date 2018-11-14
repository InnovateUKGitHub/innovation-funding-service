package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.stereotype.Component;

/**
 * TODO DW - document this class
 */
@Component
public class YourProjectLocationFormPopulator {

    private ApplicationFinanceRestService applicationFinanceRestService;

    public YourProjectLocationForm populate(long applicationId, long organisationId) {

        RestResult<ApplicationFinanceResource> applicationFinanceResourceRestResult = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId);

        String postcode = applicationFinanceResourceRestResult.handleSuccessOrFailure(
                failure -> null,
                BaseFinanceResource::getWorkPostcode);

        return new YourProjectLocationForm(postcode);
    }
}
