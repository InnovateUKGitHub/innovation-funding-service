package org.innovateuk.ifs.application.populator.finance.view;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.core.io.ByteArrayResource;

import javax.servlet.http.HttpServletRequest;

public interface FinanceFormHandler {
    ValidationMessages update(HttpServletRequest request, Long userId, Long applicationId, Long competitionId);
    ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value, Long competitionId);
    void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value, Long competitionId);
    ValidationMessages addCost(Long applicationId, Long userId, Long questionId);
    FinanceRowItem addCostWithoutPersisting(Long applicationId, Long userId, Long questionId);
    RestResult<ByteArrayResource> getFile(Long applicationFinanceId);
}
