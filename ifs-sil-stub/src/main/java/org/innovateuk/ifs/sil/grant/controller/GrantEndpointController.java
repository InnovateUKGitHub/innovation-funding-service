package org.innovateuk.ifs.sil.grant.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

@RestController
@RequestMapping("/silstub")
public class GrantEndpointController {
    private static final Log LOG = LogFactory.getLog(GrantEndpointController.class);

    @PostMapping("/sendproject")
    public RestResult<Void> sendProject(@RequestBody Grant grant) {
        LOG.info("Grant send data stub JSON : " + JsonMappingUtil.toJson(grant));
        LOG.info("Grant send data stub Summary : " + getSummary(grant));
        return restSuccess(HttpStatus.ACCEPTED);
    }

    private String getSummary(Grant grant) {
        return grant.getId() + " with " + grant.getParticipant().size() + " participants";
    }
}
