package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Interface for ApplicationFinanceTotalsSenderImpl defining security rules.
 */
public interface ApplicationFinanceTotalsSender {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'SEND_APPLICATION_TOTALS')")
    ServiceResult<Void> sendFinanceTotalsForApplication(Long applicationId);
}
