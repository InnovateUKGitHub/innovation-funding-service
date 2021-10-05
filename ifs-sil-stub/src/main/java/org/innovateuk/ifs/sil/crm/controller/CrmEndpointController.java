package org.innovateuk.ifs.sil.crm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilApplication;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * A simple endpoint to allow stubbing of the SIL outbound CRM updates.
 */
@RestController
@RequestMapping("/silstub")
public class CrmEndpointController {
    private static final Log LOG = LogFactory.getLog(CrmEndpointController.class);

    @PostMapping("/contacts")
    public RestResult<Void> updateContact(@RequestBody SilContact contact) {
        LOG.info("Stubbing out SIL CRM update contact endpoint: " + JsonMappingUtil.toJson(contact));
        return restSuccess(HttpStatus.ACCEPTED);
    }

    @PostMapping("/loanssubmission")
    public RestResult<Void> updateApplication(@RequestBody SilApplication application) {
        LOG.info("Stubbing out SIL CRM update application endpoint: " + JsonMappingUtil.toJson(application));

        if(application.getApplicationID() == null) {
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if(application.getMarkedIneligible() != null &&                  // update eligibility
                (application.getEligibilityStatusChangeDate() == null ||
                        application.getEligibilityStatusChangeSource() == null)) {
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if(application.getMarkedIneligible() == null &&                  // update application detail
                (application.getProjectTotalCost() == null &&
                        application.getProjectOtherFunding() == null)) {
            return restFailure(HttpStatus.BAD_REQUEST);
        }
        return restSuccess(HttpStatus.OK);
    }

}