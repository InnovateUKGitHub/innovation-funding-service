package com.worth.ifs.alert.service;

import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.alertResourceListType;

/**
 * AlertRestServiceImpl is a utility for CRUD operations on {@link com.worth.ifs.alert.domain.Alert}.
 * This class connects to the {@link com.worth.ifs.alert.controller.AlertController}
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
        return getWithRestResult(alertRestURL + "/findAllVisible", ParameterizedTypeReferences.alertResourceListType());
    }

    @Override
    public RestResult<List<AlertResource>> findAllVisibleByType(AlertType type) {
        return getWithRestResult(alertRestURL + "/findAllVisible/" + type.name(), ParameterizedTypeReferences.alertResourceListType());
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
