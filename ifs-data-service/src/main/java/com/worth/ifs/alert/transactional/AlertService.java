package com.worth.ifs.alert.transactional;

import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.alert.domain.Alert} data.
 */
public interface AlertService {

    @NotSecured(value="Anyone can view alerts", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AlertResource>> findAllVisible();

    @NotSecured(value="Anyone can view alerts", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AlertResource>> findAllVisibleByType(final AlertType type);

    @NotSecured(value="Anyone can view an alert", mustBeSecuredByOtherServices = false)
    ServiceResult<AlertResource> findById(final Long id);

    @PreAuthorize("hasPermission(#alertResource, 'CREATE')")
    ServiceResult<AlertResource> create(@P("alertResource") final AlertResource alertResource);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.alert.resource.AlertResource', 'DELETE')")
    ServiceResult<Void> delete(@P("id") final Long id);

    @PreAuthorize("hasPermission(filterObject, 'DELETE')")
    ServiceResult<Void> deleteAllByType(@P("type") final AlertType type);
}
