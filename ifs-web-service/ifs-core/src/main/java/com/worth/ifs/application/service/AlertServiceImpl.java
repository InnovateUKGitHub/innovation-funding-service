package com.worth.ifs.application.service;

import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.alert.service.AlertRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link AlertResource} related data,
 * through the RestService {@link AlertRestService}.
 */
@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRestService alertRestService;

    @Override
    public List<AlertResource> findAllVisible() {
        return alertRestService.findAllVisible().getSuccessObjectOrThrowException();
    }

    @Override
    public List<AlertResource> findAllVisibleByType(final AlertType alertType) {
        return alertRestService.findAllVisibleByType(alertType).getSuccessObjectOrThrowException();
    }

    @Override
    public AlertResource getById(final Long id) {
        return alertRestService.getAlertById(id).getSuccessObjectOrThrowException();
    }

    @Override
    public AlertResource create(final AlertResource alertResource) {
        return alertRestService.create(alertResource).getSuccessObjectOrThrowException();
    }

    @Override
    public void delete(final Long id) {
        alertRestService.delete(id).getSuccessObjectOrThrowException();
    }

    @Override
    public void deleteAllByType(final AlertType type) {
        alertRestService.deleteAllByType(type).getSuccessObjectOrThrowException();
    }
}
