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
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
            case ASSOCIATE_SALARY_COSTS:
                validateRowsIfNotBlank(form.getAssociateSalaryCostRows(), "associateSalaryCostRows[%s].", validationHandler);
                break;
            case ASSOCIATE_DEVELOPMENT_COSTS:
                validateRowsIfNotBlank(form.getAssociateDevelopmentCostRows(), "associateDevelopmentCostRows[%s].", validationHandler);
                validateSameNumberOfNonBlankAssociateRows(form.getAssociateDevelopmentCostRows(), form.getAssociateSalaryCostRows(), validationHandler);
                break;
            case CONSUMABLES:
                validateRows(form.getConsumableCostRows(), "consumables[%s].", validationHandler);
                break;
            case ASSOCIATE_SUPPORT:
                validateRows(form.getAssociateSupportCostRows(), "associateSupport[%s].", validationHandler);
                break;
            case KNOWLEDGE_BASE:
                validateRows(form.getKnowledgeBaseCostRows(), "knowledgeBaseCosts[%s].", validationHandler);
                break;
            case ESTATE_COSTS:
                validateEstateCosts(form.getEstateCostRows(), validationHandler);
                break;
            case ADDITIONAL_COMPANY_COSTS:
                validateForm(form.getAdditionalCompanyCostForm(), validationHandler, "additionalCompanyCostForm.");
                break;
        }
    }

    private void validateEstateCosts(Map<String, EstateCostRowForm> estateCostRows, ValidationHandler validationHandler) {
        validateRows(estateCostRows, "estateCostRows[%s].", validationHandler);
        BigInteger total = estateCostRows.values().stream()
                .map(EstateCostRowForm::getCost)
                .filter(Objects::nonNull)
                .reduce(BigInteger.ZERO, BigInteger::add);
        if (total.intValue() > 10000) {
            validationHandler.addAnyErrors(new ValidationMessages(fieldError("estateCostRows", total.intValue(), "validation.finance.maximum.estate.costs")));
        }
    }

    /*
    This is a bit ugly but its validating that if there is a missmatch in the number of blank rows between assocaite dev and associate salary that we validate the form of the missmatching blank row
     */
    private void validateSameNumberOfNonBlankAssociateRows(Map<String, AssociateDevelopmentCostRowForm> associateDevelopmentCostRows, Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows, ValidationHandler validationHandler) {
        List<Map.Entry<String, AssociateDevelopmentCostRowForm>> blankDevRows = associateDevelopmentCostRows.entrySet().stream().filter(entry -> entry.getValue().isBlank()).collect(Collectors.toList());
        List<Map.Entry<String, AssociateSalaryCostRowForm>> blankSalRows = associateSalaryCostRows.entrySet().stream().filter(entry -> entry.getValue().isBlank()).collect(Collectors.toList());
        if (blankDevRows.size() == 1 && blankSalRows.size() == 0) {
            validateForm(blankDevRows.get(0).getValue(), validationHandler,"associateDevelopmentCostRows[%s].", blankDevRows.get(0).getKey());
        } else if (blankDevRows.size() == 0 && blankSalRows.size() == 1) {
            validateForm(blankSalRows.get(0).getValue(), validationHandler,"associateSalaryCostRows[%s].", blankSalRows.get(0).getKey());
        }
        // both having 1 blank row, or both having 0 blank rows is valid.
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

    private <R extends AbstractCostRowForm> void validateRowsIfNotBlank(Map<String, R> rows, String path, ValidationHandler validationHandler) {
        rows.forEach((id, row) -> {
            if (!row.isBlank()) {
                validateForm(row, validationHandler, path, id);
            }
        });
    }

    private <R> void validateForm(R form, ValidationHandler validationHandler, String path, Object... arguments) {
        Set<ConstraintViolation<R>> constraintViolations = validator.validate(form);
        validationHandler.addAnyErrors(new ValidationMessages(constraintViolations), toFieldErrorWithPath(String.format(path, arguments)), defaultConverters());

    }
}
