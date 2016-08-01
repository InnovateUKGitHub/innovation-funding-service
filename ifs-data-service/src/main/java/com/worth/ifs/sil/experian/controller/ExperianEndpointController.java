package com.worth.ifs.sil.experian.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.sil.experian.resource.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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

    private static HashMap<String, List<Condition>> errorExamples;
    private static HashMap<AccountDetails, VerificationResultWrapper> verificationResults;
    private static VerificationResultWrapper defaultVerificationResult;

    static {
        final String WARNING = "warning";
        final String ERROR = "warning";
        final String INFO = "information";

        errorExamples = new HashMap<>();
        errorExamples.put("000003-12", singletonList(new Condition(ERROR, 4, "Account number format is incorrect")));
        errorExamples.put("000003-12345673", singletonList(new Condition(ERROR, 7, "Modulus check has failed")));
        errorExamples.put("000003-123 45672", singletonList(new Condition(WARNING, 1, "Account details were not in standard form and have been transposed")));
        errorExamples.put("000003-22345616", singletonList(new Condition(WARNING, 5, "Account does not support Direct Debit transactions")));
        errorExamples.put("000003-22345632", singletonList(new Condition(WARNING, 7, "Account does not support Direct Credit transactions")));
        errorExamples.put("000003-22345624", singletonList(new Condition(WARNING, 65, "Collection account requires a reference or roll account number")));
        errorExamples.put("000003-22345683", singletonList(new Condition(WARNING, 78, "Account does not support AUDDIS transactions")));
        errorExamples.put("000004-22345610", singletonList(new Condition(INFO, 78, "Alternate information is available for this account")));
        errorExamples.put("123456-12345678", asList(new Condition(WARNING, 2, "Modulus check algorithm is unavailable for these account details"), new Condition(ERROR, 6, "Modulus check algorithm is unavailable for these account details")));

        verificationResults = new HashMap<>();
        verificationResults.put(
                new AccountDetails("404750","51406795", "Vitruvius Stonework Limited","M60674010",
                        new Address("", "Springbank Chapelgreen", "Charlmont Road", "", "", "SW17 9AB")
                ),
                new VerificationResultWrapper(new VerificationResult("7", "8", "9", "No Match",
                        singletonList(new Condition("warning", 2, "Modulus check algorithm is unavailable for these account details"))
                ))
        );

        verificationResults.put(
                new AccountDetails("090127","78132557", "Consumed By Riffage Ltd","06477798",
                        new Address(null, "1", "Riff Street", null, "Bath", "BA1 5LR")
                ),
                new VerificationResultWrapper(new VerificationResult("9", "8", "6", "No Match", emptyList()))
        );

        defaultVerificationResult = new VerificationResultWrapper(new VerificationResult("7", "7", "7", "Match", singletonList(new Condition("warning", 2, "Modulus check algorithm is unavailable for these account details"))));
    }

    @RequestMapping(value="/experianValidate", method = POST)
    public RestResult<ValidationResultWrapper> experianValidate(@RequestBody AccountDetails accountDetails){
        LOG.info("Stubbing out SIL experian validation: " + accountDetails);
        final ValidationResult validationResult = new ValidationResult();

        List<Condition> invalidConditions = errorExamples.get(accountDetails.getSortcode() + "-" + accountDetails.getAccountNumber());
        if(invalidConditions != null) {
            validationResult.setCheckPassed(false);
            validationResult.setIban(null);
            validationResult.setConditions(invalidConditions);
        } else {
            validationResult.setCheckPassed(true);
            validationResult.setConditions(emptyList());
        }
        return restSuccess(new ValidationResultWrapper(validationResult));
    }

    @RequestMapping(value="/experianVerify", method = POST)
    public RestResult<VerificationResultWrapper> experianVerify(@RequestBody AccountDetails accountDetails){
        VerificationResultWrapper verificationResultWrapper = verificationResults.get(accountDetails);
        if(verificationResultWrapper != null) {
            return restSuccess(verificationResultWrapper);
        } else {
            return restSuccess(defaultVerificationResult);
        }
    }
}