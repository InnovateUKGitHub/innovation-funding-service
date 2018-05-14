package org.innovateuk.ifs.service;

import org.innovateuk.ifs.resource.AlertType;
import org.innovateuk.ifs.resource.AlertResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.alert.domain.Alert} related data.
*/
public interface AlertRestService {

    RestResult<List<AlertResource>> findAllVisible();

    RestResult<List<AlertResource>> findAllVisibleByType(final AlertType type);

    RestResult<AlertResource> getAlertById(final Long id);

    RestResult<AlertResource> create(final AlertResource alertResource);

    RestResult<Void> delete(final Long id);

    RestResult<Void> deleteAllByType(final AlertType type);
}
