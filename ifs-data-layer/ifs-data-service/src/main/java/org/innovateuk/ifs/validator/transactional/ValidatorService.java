package org.innovateuk.ifs.validator.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface ValidatorService {
    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    List<BindingResult> validateFormInputResponse(Long applicationId, Long formInputId);

    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    BindingResult validateFormInputResponse(Long applicationId, Long formInputId, Long markedAsCompleteById);

    @NotSecured(value = "This service is used to validate existing data", mustBeSecuredByOtherServices = false)
    List<ValidationMessages> validateCostItem(Long applicationId, Question question, Long markedAsCompleteById);

    @NotSecured(value = "This is not getting date from the database, just getting a FinanceRowHandler", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(FinanceRowItem costItem);

    @NotSecured(value = "This is not getting date from the database, just getting a FinanceRowHandler for project finance", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getProjectCostHandler(FinanceRowItem costItem);
}
