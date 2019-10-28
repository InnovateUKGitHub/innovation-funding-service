package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface ApplicationValidatorService {
    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    List<BindingResult> validateFormInputResponse(Long applicationId, Long formInputId);

    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    ValidationMessages validateFormInputResponse(Application application, long formInputId, long markedAsCompleteById);

    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    List<ValidationMessages> validateCostItem(Long applicationId, FinanceRowType type, Long markedAsCompleteById);

    @NotSecured(value = "This is not getting date from the database, just getting a FinanceRowHandler", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(FinanceRowItem costItem);

    @NotSecured(value = "This is not getting date from the database, just getting a FinanceRowHandler for project finance", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getProjectCostHandler(FinanceRowItem costItem);

    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    ValidationMessages validateAcademicUpload(Application application, Long markedAsCompleteById);
}
