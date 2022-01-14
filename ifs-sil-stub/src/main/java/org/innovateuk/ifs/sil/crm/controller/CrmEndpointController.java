package org.innovateuk.ifs.sil.crm.controller;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.innovateuk.ifs.util.JsonMappingDeprecatedUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;


/**
 * A simple endpoint to allow stubbing of the SIL outbound CRM updates.
 */
@Slf4j
@RestController
@RequestMapping("/silstub")
public class CrmEndpointController {

    @PostMapping("/contacts")
    public RestResult<Void> updateContact(@RequestBody SilContact contact) {
        log.info("Stubbing out SIL CRM update contact endpoint: " + JsonMappingDeprecatedUtil.toJson(contact));
        return restSuccess(HttpStatus.ACCEPTED);
    }


    @PostMapping("/loanssubmission")
    public RestResult<Void> updateApplication(@RequestBody SilLoanApplication application) {
        log.info("Stubbing out SIL CRM update application endpoint: " + JsonMappingDeprecatedUtil.toJson(application));

        if (application.getApplicationID() == null) {
            log.error("application id is null");
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if (application.getMarkedIneligible() != null &&                  // update eligibility
                (application.getEligibilityStatusChangeDate() == null ||
                        application.getEligibilityStatusChangeSource() == null)) {
            log.error("update eligibility is incomplete");
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if (application.getMarkedIneligible() == null &&                  // update application detail
                (application.getProjectTotalCost() == null &&
                        application.getProjectOtherFunding() == null)) {
            log.error("update application detail is incomplete");
            return restFailure(HttpStatus.BAD_REQUEST);
        }
        return restSuccess(HttpStatus.ACCEPTED);
    }

    @PostMapping("/decisionmatrix")
    public RestResult<Void> updateApplication(@RequestBody SilLoanAssessment assessment) {
        log.info("Stubbing out SIL CRM update loan assessment endpoint: " + JsonMappingDeprecatedUtil.toJson(assessment));

        if(assessment.getCompetitionID() == null) {
            log.error("competition id is null");
            return restFailure(HttpStatus.BAD_REQUEST);
        } else if(assessment.getApplications() == null) {
            log.error("applications is null");
            return restFailure(HttpStatus.BAD_REQUEST);
        }
        return restSuccess(HttpStatus.ACCEPTED);
    }
}
