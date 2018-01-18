package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface FinanceTotalsSender {
    ServiceResult<Void> syncFinanceTotalsForApplication(Long applicationId);
}
