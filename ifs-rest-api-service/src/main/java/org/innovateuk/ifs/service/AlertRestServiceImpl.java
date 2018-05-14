package org.innovateuk.ifs.service;

import org.innovateuk.ifs.resource.AlertType;
import org.innovateuk.ifs.resource.AlertResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AlertRestServiceImpl is a utility for CRUD operations on {@link org.innovateuk.ifs.alert.domain.Alert}.
 * This class connects to the {@link org.innovateuk.ifs.alert.controller.AlertController}
 * through a REST call.
 */
@Service
public class AlertRestServiceImpl extends BaseRestService implements AlertRestService {

    private String alertRestURL = "/alert";

    protected void setAlertRestURL(final String alertRestURL) {
        this.alertRestURL = alertRestURL;
    }

    @Override
    public RestResult<List<AlertResource>> findAllVisible() {
        return getWithRestResultAnonymous(alertRestURL + "/findAllVisible", ParameterizedTypeReferences.alertResourceListType());
    }

    @Override
    public RestResult<List<AlertResource>> findAllVisibleByType(AlertType type) {
        return getWithRestResultAnonymous(alertRestURL + "/findAllVisible/" + type.name(), ParameterizedTypeReferences.alertResourceListType());
    }

    @Override
    public RestResult<AlertResource> getAlertById(final Long id) {
        return getWithRestResult(alertRestURL + "/" + id, AlertResource.class);
    }


    @Override
    public RestResult<AlertResource> create(final AlertResource alertResource) {
        return postWithRestResult(alertRestURL + "/", alertResource, AlertResource.class);
    }

    @Override
    public RestResult<Void> delete(final Long id) {
        return deleteWithRestResult(alertRestURL + "/" + id);
    }

    @Override
    public RestResult<Void> deleteAllByType(final AlertType type) {
        return deleteWithRestResult(alertRestURL + "/delete/" + type.name());
    }
}
