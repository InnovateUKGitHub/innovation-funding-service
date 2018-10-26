package org.innovateuk.ifs.sil.grant.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.experian.controller.ExperianEndpointController;
import org.innovateuk.ifs.sil.grant.resource.Project;
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
    public RestResult<Void> sendProject(@RequestBody Project project) {
        LOG.info("Grant send data stub JSON : " + JsonMappingUtil.toJson(project));
        LOG.info("Grant send data stub Summary : " + getSummary(project));
        return restSuccess(HttpStatus.ACCEPTED);
    }

    private String getSummary(Project project) {
        return project.getId() + " with " + project.getParticipant().size() + " participants";
    }
}
