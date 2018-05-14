package org.innovateuk.ifs.sil.email.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.springframework.http.HttpStatus.ACCEPTED;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for integration tests only
 */
@RestController
@RequestMapping("/silstub/sendmail")
@Profile({"integration-test"})
public class SimpleEmailEndpointController {

    @PostMapping
    public RestResult<Void> sendMail(@RequestBody SilEmailMessage message) {
        return restSuccess(ACCEPTED);
    }
}
