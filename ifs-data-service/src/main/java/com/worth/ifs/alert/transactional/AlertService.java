package com.worth.ifs.alert.transactional;

import com.worth.ifs.alert.domain.AlertType;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.alert.domain.Alert} data.
 */
public interface AlertService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<AlertResource>> findAllVisible();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<AlertResource>> findAllVisibleByType(@P("type") final AlertType type);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.alert.resource.AlertResource', 'READ')")
    ServiceResult<AlertResource> findById(@P("id") final Long id);

    @PreAuthorize("hasPermission(#alertResource, 'CREATE')")
    ServiceResult<AlertResource> create(@P("alertResource") final AlertResource alertResource);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.alert.resource.AlertResource', 'DELETE')")
    ServiceResult<Void> delete(@P("id") final Long id);

    @PreAuthorize("hasPermission(filterObject, 'DELETE')")
    ServiceResult<Void> deleteAllByType(@P("type") final AlertType type);
}
