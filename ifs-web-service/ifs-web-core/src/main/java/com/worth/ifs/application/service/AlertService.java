package com.worth.ifs.application.service;

import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.resource.AlertResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AlertResource} related data.
 */
public interface AlertService {

    List<AlertResource> findAllVisible();

    List<AlertResource> findAllVisibleByType(final AlertType alertType);

    AlertResource getById(final Long id);

    AlertResource create(final AlertResource alertResource);

    void delete(final Long id);

    void deleteAllByType(final AlertType type);

}
