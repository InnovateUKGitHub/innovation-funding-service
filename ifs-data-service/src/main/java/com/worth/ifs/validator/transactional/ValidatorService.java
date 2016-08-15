package com.worth.ifs.validator.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.handler.item.FinanceRowHandler;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.security.NotSecured;
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
}
