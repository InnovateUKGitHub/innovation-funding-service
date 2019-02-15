package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;

/**
 * Security annotated interface for {@ApplicationProgressServiceImpl}.
 */
public interface ApplicationProgressService {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<BigDecimal> updateApplicationProgress(final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    boolean applicationReadyForSubmit(@P("applicationId") final Long applicationId);
}
