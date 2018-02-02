package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Interface for ApplicationFinanceTotalsSenderImpl defining security rules.
 */
public interface ApplicationFinanceTotalsSender {
    @PreAuthorize("hasPermission(#applicationId, 'READ')")
    ServiceResult<Void> sendFinanceTotalsForApplication(@P("applicationId") Long applicationId);
}
