package com.worth.ifs.sil.experian.controller;

import com.worth.ifs.bankdetails.resource.experian.AccountDetails;
import com.worth.ifs.bankdetails.resource.experian.ValidationResult;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for non-integration test environments
 */
@RestController
@RequestMapping("/silstub")
public class ExperianEndpointController {
    @Value("${sil.stub.send.mail.from.ifs:false}")
    private Boolean validateFromSilStub;

    @RequestMapping(value="/experianValidate", method = POST)
    public RestResult<ValidationResult> experianValidate(@RequestBody AccountDetails accountDetails){
        return null;
    }

    @RequestMapping(value="/experianVerify", method = POST)
    public RestResult<Void> experianVerify(@RequestBody AccountDetails accountDetails){
        return null;
    }
}
