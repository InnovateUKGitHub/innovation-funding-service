package org.innovateuk.ifs.alert.service;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
public class AlertRestServiceImpl extends BaseRestService implements AlertRestService {

    private static final Log LOG = LogFactory.getLog(AlertRestServiceImpl.class);


    private String alertRestURL = "/alert";

    protected void setAlertRestURL(final String alertRestURL) {
        this.alertRestURL = alertRestURL;
    }

    @Value("${ifs.alert.service.rest.baseURL}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    @HystrixCommand(fallbackMethod = "findAllVisibleFallback")
    public RestResult<List<AlertResource>> findAllVisible() {
        return getWithRestResultAnonymous(alertRestURL + "/findAllVisible", ParameterizedTypeReferences.alertResourceListType());
    }

    public RestResult<List<AlertResource>> findAllVisibleFallback( Throwable e) {
        LOG.info("Calling Alerts Fallback:",e);
        return RestResult.restSuccess(Collections.emptyList());
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
