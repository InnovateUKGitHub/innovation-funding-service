package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.core.io.ByteArrayResource;

import javax.servlet.http.HttpServletRequest;

public interface FinanceFormHandler {
    FinanceRowItem addCostWithoutPersisting(Long applicationId, Long userId, Long questionId);
    RestResult<ByteArrayResource> getFile(Long applicationFinanceId);
}
