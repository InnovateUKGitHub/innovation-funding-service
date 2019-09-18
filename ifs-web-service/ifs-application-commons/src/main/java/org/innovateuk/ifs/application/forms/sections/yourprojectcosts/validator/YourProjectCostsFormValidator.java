package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.validator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.newFieldError;

@Component
public class YourProjectCostsFormValidator {

    @Autowired
    private Validator validator;

    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;


    public void validateType(YourProjectCostsForm form, FinanceRowType type, ValidationHandler validationHandler) {
        switch (type) {
            case LABOUR:
                validateLabour(form.getLabour(), validationHandler);
                break;
            case OVERHEADS:
                validateOverhead(form.getOverhead(), validationHandler);
                break;
            case PROCUREMENT_OVERHEADS:
                validateRows(form.getProcurementOverheadRows(),"procurementOverheadRows[%s].", validationHandler);
                break;
            case CAPITAL_USAGE:
                validateRows(form.getCapitalUsageRows(), "capitalUsageRows[%s].", validationHandler);
                break;
            case MATERIALS:
                validateRows(form.getMaterialRows(), "materialRows[%s].", validationHandler);
                break;
            case OTHER_COSTS:
                validateRows(form.getOtherRows(), "otherRows[%s].", validationHandler);
                break;
            case SUBCONTRACTING_COSTS:
                validateRows(form.getSubcontractingRows(), "subcontractingRows[%s].", validationHandler);
                break;
            case TRAVEL:
                validateRows(form.getTravelRows(), "travelRows[%s].", validationHandler);
                break;
            case VAT:
                validateVat(form.getVatForm(), validationHandler);
        }
    }

    public void validate(long applicationId, YourProjectCostsForm form, ValidationHandler validationHandler) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        competition.getFinanceRowTypes().forEach(type -> validateType(form, type, validationHandler));
    }

    private void validateOverhead(OverheadForm overhead, ValidationHandler validationHandler) {
        if (OverheadRateType.TOTAL.equals(overhead.getRateType())) {
            validateForm(overhead, validationHandler, "overhead.");
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            if (!user.isInternalUser()) {
                boolean hasOverheadFile = overheadFileRestService.getOverheadFileDetails(overhead.getCostId()).isSuccess();
                if (!hasOverheadFile) {
                    validationHandler.addAnyErrors(new ValidationMessages(fieldError("overhead.file", null, "validation.finance.overhead.file.required")));
                }
            }
        }
    }

    private void validateLabour(LabourForm labour, ValidationHandler validationHandler) {
        validateForm(labour, validationHandler, "labour.");
        validateRows(labour.getRows(), "labour.rows[%s].", validationHandler);
    }

    private void validateVat(VatForm vatForm, ValidationHandler validationHandler) {
        if (vatForm == null) {
            validationHandler.addAnyErrors(new ValidationMessages(fieldError("vatForm.registered", null, "validation.yourProjectCostsForm.vatRegistered.required")));
        }
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
            if (!(id.startsWith(UNSAVED_ROW_PREFIX) && row.isBlank())) {
                validateForm(row, validationHandler, path, id);
            }
        });
    }

    private <R> void validateForm(R form, ValidationHandler validationHandler, String path, Object... arguments) {
        Set<ConstraintViolation<R>> constraintViolations = validator.validate(form);
        validationHandler.addAnyErrors(new ValidationMessages(constraintViolations), toFieldErrorWithPath(String.format(path, arguments)), defaultConverters());

    }
}
