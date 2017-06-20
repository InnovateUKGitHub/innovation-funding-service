package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.reject;

/**
 * This class validates the FormInputResponse, it checks if there is a value present.
 */
@Component
public class AcademicJesValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FormInputResponse.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final FormInputResponse response = (FormInputResponse) target;

        if (FormInputType.FINANCE_UPLOAD.equals(response.getFormInput().getType())) {
            if (responseIsEmpty(response) || financeFileIsEmpty(response)) {
                reject(errors, "validation.application.jes.upload.required");
            }
        }
    }

    private boolean financeFileIsEmpty(FormInputResponse response) {
        List<ApplicationFinance> applicationFinances = response.getApplication().getApplicationFinances();

        if(applicationFinances == null) {
            return true;
        }

        Optional<ApplicationFinance> applicationFinanceOpt = applicationFinances
                .stream()
                .filter(applicationFinance -> applicationFinance.getOrganisation().getId().equals(response.getUpdatedBy().getOrganisationId()))
                .findAny();

        return !applicationFinanceOpt.isPresent() || applicationFinanceOpt.get().getFinanceFileEntry() == null;
    }

    private boolean responseIsEmpty(FormInputResponse response) {
        return StringUtils.isEmpty(response.getValue()) || "".equals(response.getValue().trim());
    }


}
