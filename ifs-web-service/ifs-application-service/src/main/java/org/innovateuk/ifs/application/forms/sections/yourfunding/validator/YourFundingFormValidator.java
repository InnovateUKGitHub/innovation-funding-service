package org.innovateuk.ifs.application.forms.sections.yourfunding.validator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.function.Supplier;

@Component
public class YourFundingFormValidator extends AbstractYourFundingFormValidator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public void validate(AbstractYourFundingForm form, Errors errors, UserResource user, long applicationId) {
        Supplier<BaseFinanceResource> financeSupplier = () -> {
            OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
            return applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
        };
        validate(form, errors, financeSupplier);
    }
}
