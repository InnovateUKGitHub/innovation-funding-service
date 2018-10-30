package org.innovateuk.ifs.sil.grant.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_INVALID_ARGUMENT;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

@RestController
@RequestMapping("/silstub")
public class GrantEndpointController {
    private static final Log LOG = LogFactory.getLog(GrantEndpointController.class);

    @PostMapping("/sendproject")
    public RestResult<Void> sendProject(@RequestBody Grant grant) {
        LOG.info("Grant data send to stub : JSON = " + JsonMappingUtil.toJson(grant));
        List<String> errors = new GrantValidator().checkForErrors(grant);
        if (errors.isEmpty()) {
            LOG.info("Grant data sent to stub : Summary = " + getSummary(grant));
            return restSuccess(HttpStatus.ACCEPTED);
        }
        LOG.warn("Grant data was invalid : " + String.join(",", errors));
        return restFailure(Error.globalError(GENERAL_INVALID_ARGUMENT.getErrorKey(),
                new ArrayList<>(errors)));
    }

    private String getSummary(Grant grant) {
        return grant.getId() + " with " + grant.getParticipants().size() + " participants";
    }
}
