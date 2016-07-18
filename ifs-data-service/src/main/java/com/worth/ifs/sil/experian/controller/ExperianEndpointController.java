package com.worth.ifs.sil.experian.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.Condition;
import com.worth.ifs.sil.experian.resource.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static org.hibernate.jpa.internal.QueryImpl.LOG;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for non-integration test environments
 */
@RestController
@RequestMapping("/silstub")
public class ExperianEndpointController {
    @Value("${sil.stub.experian.validate:true}")
    private Boolean validateFromSilStub;

    @RequestMapping(value="/experianValidate", method = POST)
    public RestResult<ValidationResult> experianValidate(@RequestBody AccountDetails accountDetails){
        LOG.info("Stubbing out SIL experian validation: " + accountDetails);
        ValidationResult validationResult = new ValidationResult();
        if(accountDetails.getSortcode().equals("123456") && accountDetails.getAccountNumber().equals("12345678")) {
            validationResult.setCheckPassed(false);
            validationResult.setIban(null);
            validationResult.setConditions(buildModChkAlgUnavilableConditions());
        } else {
            validationResult.setCheckPassed(true);
        }
        return restSuccess(validationResult);
    }

    @RequestMapping(value="/experianVerify", method = POST)
    public RestResult<Void> experianVerify(@RequestBody AccountDetails accountDetails){
        return null;
    }

    private List<Condition> buildModChkAlgUnavilableConditions() {
        Condition warning = new Condition("warning", "2", "Modulus check algorithm is unavailable for these account details");
        Condition error = new Condition("error", "6", "Modulus check algorithm is unavailable for these account details");
        return asList(warning, error);
    }
}