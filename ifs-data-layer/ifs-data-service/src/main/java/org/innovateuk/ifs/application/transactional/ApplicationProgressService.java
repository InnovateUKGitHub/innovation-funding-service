package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;

public interface ApplicationProgressService {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<BigDecimal> updateApplicationProgress(final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    boolean applicationReadyForSubmit(@P("applicationId") final Long applicationId);
}
