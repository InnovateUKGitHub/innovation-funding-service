package org.innovateuk.ifs.sil.grant.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.util.Either;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_PROCESS_SEND_FAILED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;

@Component
public class RestGrantEndpoint implements GrantEndpoint {
    private static final Log LOG = LogFactory.getLog(RestGrantEndpoint.class);

    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.baseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.grantSend:/accprojects}")
    private String path;

    @Override
    public ServiceResult<Void> send(Grant grant) {

        List<Grant> grantAsList = singletonList(grant);

        Either<ResponseEntity<Void>, ResponseEntity<JsonNode>> response =
                adaptor.restPostWithEntity(silRestServiceUrl + path, grantAsList,
                        JsonNode.class, Void.class, HttpStatus.OK, HttpStatus.ACCEPTED);

        return response.mapLeftOrRight(
                failure -> {
                    LOG.debug("Sent grant FAILURE : " + toJson(grant));
                    return serviceFailure(new Error(GRANT_PROCESS_SEND_FAILED, failure.getStatusCode()));
                },
                success -> {
                    LOG.debug("Sent grant SUCCESS : " + toJson(grant));
                    return serviceSuccess();
                }
        );
    }
}
