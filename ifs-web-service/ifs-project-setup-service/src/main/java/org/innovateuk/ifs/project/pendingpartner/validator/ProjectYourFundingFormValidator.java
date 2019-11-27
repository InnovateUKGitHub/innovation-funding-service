package org.innovateuk.ifs.project.pendingpartner.validator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.validator.AbstractYourFundingFormValidator;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.function.Supplier;

@Component
public class ProjectYourFundingFormValidator extends AbstractYourFundingFormValidator {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    public void validate(AbstractYourFundingForm form, Errors errors, long projectId, long organisationId) {
        Supplier<BaseFinanceResource> financeSupplier = () -> projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        validate(form, errors, financeSupplier);
    }
}
