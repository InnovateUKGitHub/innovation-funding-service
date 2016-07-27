package com.worth.ifs.application.finance.view;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.springframework.core.io.ByteArrayResource;

import javax.servlet.http.HttpServletRequest;

public interface FinanceFormHandler {
    ValidationMessages update(HttpServletRequest request, Long userId, Long applicationId);
    ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value);
    void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value);
    CostItem addCost(Long applicationId, Long userId, Long questionId);
    RestResult<ByteArrayResource> getFile(Long applicationFinanceId);
}
