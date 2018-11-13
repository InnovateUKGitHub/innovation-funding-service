package org.innovateuk.ifs.application.forms.yourprojectcosts.validator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.OverheadForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.EMPTY_ROW_ID;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.newFieldError;

@Component
public class YourProjectCostsFormValidator {

    @Autowired
    private Validator validator;

    public void validate(YourProjectCostsForm form, ValidationHandler validationHandler) {
        validateRows(form.getLabourCosts(), "labourCosts[%s].%s", validationHandler);
        if (OverheadRateType.CUSTOM_RATE.equals(form.getOverhead().getRateType())) {
            Set<ConstraintViolation<OverheadForm>> constraintViolations = validator.validate(form.getOverhead());
            validationHandler.addAnyErrors(new ValidationMessages(constraintViolations), toFieldErrorWithPath("overhead", ""), defaultConverters());
        }
        validateRows(form.getMaterialRows(), "materialRows[%s].%s", validationHandler);
        validateRows(form.getCapitalUsageRows(), "capitalUsageRows[%s].%s", validationHandler);
        validateRows(form.getSubcontractingRows(), "subcontractingRows[%s].%s", validationHandler);
        validateRows(form.getTravelRows(), "travelRows[%s].%s", validationHandler);
        validateRows(form.getOtherRows(), "otherRows[%s].%s", validationHandler);
    }

    private ErrorToObjectErrorConverter toFieldErrorWithPath(String path, String id) {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(newFieldError(e, String.format(path, id, e.getFieldName()), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    private <R extends AbstractCostRowForm> void validateRows(Map<String, R> rows, String path, ValidationHandler validationHandler) {
        rows.forEach((id, row) -> {
            Set<ConstraintViolation<R>> constraintViolations = Collections.emptySet();
            if (!(EMPTY_ROW_ID.equals(id) && row.isBlank())) {
                constraintViolations = validator.validate(row);
            }
            validationHandler.addAnyErrors(new ValidationMessages(constraintViolations), toFieldErrorWithPath(path, id), defaultConverters());
        });
    }
}
