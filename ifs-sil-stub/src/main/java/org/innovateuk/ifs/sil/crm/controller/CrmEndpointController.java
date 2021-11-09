package org.innovateuk.ifs.sil.crm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;


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
    public RestResult<Void> updateApplication(@RequestBody SilLoanApplication application) {
        LOG.info("Stubbing out SIL CRM update application endpoint: " + JsonMappingUtil.toJson(application));

        if (application.getApplicationID() == null) {
            LOG.error("application id is null");
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if (application.getMarkedIneligible() != null &&                  // update eligibility
                (application.getEligibilityStatusChangeDate() == null ||
                        application.getEligibilityStatusChangeSource() == null)) {
            LOG.error("update eligibility is incomplete");
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if (application.getMarkedIneligible() == null &&                  // update application detail
                (application.getProjectTotalCost() == null &&
                        application.getProjectOtherFunding() == null)) {
            LOG.error("update application detail is incomplete");
            return restFailure(HttpStatus.BAD_REQUEST);
        }
        return restSuccess(HttpStatus.ACCEPTED);
    }


}
