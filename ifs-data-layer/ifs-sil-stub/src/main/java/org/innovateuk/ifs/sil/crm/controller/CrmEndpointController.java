package org.innovateuk.ifs.sil.crm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * A simple endpoint to allow stubbing of the SIL outbound CRM updates.
 */
@RestController
@RequestMapping("/silstub")
@Profile("!crm")
public class CrmEndpointController {
    private static final Log LOG = LogFactory.getLog(CrmEndpointController.class);

    @PostMapping("/contacts")
    public RestResult<Void> updateContact(@RequestBody SilContact contact) {
        LOG.info("Stubbing out SIL CRM update contact endpoint: " + JsonMappingUtil.toJson(contact));
        return restSuccess(HttpStatus.ACCEPTED);
    }

}
