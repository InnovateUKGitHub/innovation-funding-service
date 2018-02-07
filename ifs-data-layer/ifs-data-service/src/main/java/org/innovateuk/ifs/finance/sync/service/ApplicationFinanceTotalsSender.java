package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Interface for ApplicationFinanceTotalsSenderImpl defining security rules.
 */
public interface ApplicationFinanceTotalsSender {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'SEND_APPLICATION_TOTALS_ON_SUBMIT')")
    ServiceResult<Void> sendFinanceTotalsForApplication(@P("applicationId") Long applicationId);
}
