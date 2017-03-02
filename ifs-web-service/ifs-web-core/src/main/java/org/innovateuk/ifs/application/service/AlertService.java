package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AlertResource} related data.
 */
public interface AlertService {

    List<AlertResource> findAllVisible();

    List<AlertResource> findAllVisibleByType(final AlertType alertType);

    AlertResource getById(final Long id);

    ServiceResult<AlertResource> create(final AlertResource alertResource);

    ServiceResult<Void> delete(final Long id);

    ServiceResult<Void> deleteAllByType(final AlertType type);

}
