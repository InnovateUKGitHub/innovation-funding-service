package com.worth.ifs.alert.service;

import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.alert.domain.Alert} related data.
*/
public interface AlertRestService {

    RestResult<List<AlertResource>> findAllVisible();

    RestResult<List<AlertResource>> findAllVisibleByType(final AlertType type);

    RestResult<AlertResource> getAlertById(final Long id);

    RestResult<AlertResource> create(final AlertResource alertResource);

    RestResult<Void> delete(final Long id);

    RestResult<Void> deleteAllByType(final AlertType type);
}
