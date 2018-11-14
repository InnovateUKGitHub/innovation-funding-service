package org.innovateuk.ifs.application.forms.yourprojectcosts.validator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.LabourForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
        validateLabour(form.getLabour(), validationHandler);
        if (OverheadRateType.CUSTOM_RATE.equals(form.getOverhead().getRateType())) {
            validateForm(form.getOverhead(), validationHandler, "overhead.");
        }
        validateRows(form.getMaterialRows(), "materialRows[%s].", validationHandler);
        validateRows(form.getCapitalUsageRows(), "capitalUsageRows[%s].", validationHandler);
        validateRows(form.getSubcontractingRows(), "subcontractingRows[%s].", validationHandler);
        validateRows(form.getTravelRows(), "travelRows[%s].", validationHandler);
        validateRows(form.getOtherRows(), "otherRows[%s].", validationHandler);
    }

    private void validateLabour(LabourForm labour, ValidationHandler validationHandler) {
        validateForm(labour, validationHandler, "labour.");
        validateRows(labour.getRows(), "labour.labourCosts[%s].", validationHandler);
    }

    private ErrorToObjectErrorConverter toFieldErrorWithPath(String path) {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(newFieldError(e, path + e.getFieldName(), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    private <R extends AbstractCostRowForm> void validateRows(Map<String, R> rows, String path, ValidationHandler validationHandler) {
        rows.forEach((id, row) -> {
            if (!(EMPTY_ROW_ID.equals(id) && row.isBlank())) {
                validateForm(row, validationHandler,  path, id);
            }
        });
    }

    private <R> void validateForm(R form, ValidationHandler validationHandler, String path, Object... arguments) {
        Set<ConstraintViolation<R>> constraintViolations = validator.validate(form);
        validationHandler.addAnyErrors(new ValidationMessages(constraintViolations), toFieldErrorWithPath(String.format(path, arguments)), defaultConverters());

    }
}
