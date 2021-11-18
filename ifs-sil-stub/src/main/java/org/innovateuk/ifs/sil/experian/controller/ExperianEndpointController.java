package org.innovateuk.ifs.sil.experian.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.experian.resource.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.util.JsonMappingDeprecatedUtil.fromJson;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for non-integration test environments
 */
@RestController
@RequestMapping("/silstub")
public class ExperianEndpointController {
    private static final Log LOG = LogFactory.getLog(ExperianEndpointController.class);

    static final Map<SILBankDetails, ValidationResultWrapper> VALIDATION_ERRORS;
    private static final Map<SILBankDetails, SilExperianError> OTHER_ERRORS_DURING_VALIDATION;
    private static final ValidationResultWrapper DEFAULT_VALIDATION_RESULT;
    static final Map<AccountDetails, VerificationResultWrapper> VERIFICATION_RESULTS;
    private static final Map<AccountDetails, SilExperianError> OTHER_ERRORS_DURING_VERIFICATION;
    private static final VerificationResultWrapper DEFAULT_VERIFICATION_RESULT;

    static {
        VALIDATION_ERRORS = buildValidationErrors();
        DEFAULT_VALIDATION_RESULT =
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB53BRNU00000412345677\"\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class);

        OTHER_ERRORS_DURING_VALIDATION = new HashMap<>();
        VERIFICATION_RESULTS = buildVerificationResults();
        OTHER_ERRORS_DURING_VERIFICATION = buildOtherErrorsDuringVerification();
        DEFAULT_VERIFICATION_RESULT = new VerificationResultWrapper(new VerificationResult("1", "7", "3", "No Match", singletonList(new Condition("warning", 2, "Modulus check algorithm is unavailable for these account details"))));
    }

    @PostMapping("/experianValidate")
    public RestResult<Object> experianValidate(@RequestBody SILBankDetails bankDetails) {
        LOG.info("Stubbing out SIL experian validation: " + bankDetails);
        ValidationResultWrapper validationResultWrapper = VALIDATION_ERRORS.get(bankDetails);
        if (validationResultWrapper == null) {
            SilExperianError silError = OTHER_ERRORS_DURING_VALIDATION.get(bankDetails);
            if(silError == null) {
                validationResultWrapper = DEFAULT_VALIDATION_RESULT;
            } else {
                return restSuccess(silError);
            }
        }
        return restSuccess(validationResultWrapper);
    }

    @PostMapping("/experianVerify")
    public RestResult<Object> experianVerify(@RequestBody AccountDetails accountDetails) {
        VerificationResultWrapper verificationResultWrapper = VERIFICATION_RESULTS.get(accountDetails);
        if (verificationResultWrapper == null) {
            SilExperianError silError = OTHER_ERRORS_DURING_VERIFICATION.get(accountDetails);
            if(silError == null) {
                verificationResultWrapper = DEFAULT_VERIFICATION_RESULT;
            } else {
                return restSuccess(silError);
            }
        }
        return restSuccess(verificationResultWrapper);
    }

    private static Map<SILBankDetails, ValidationResultWrapper> buildValidationErrors() {
        Map<SILBankDetails, ValidationResultWrapper> validationErrors = new HashMap<>();
        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"12\"\n" +
                        "}", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": false,\n" +
                        "    \"iban\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"error\",\n" +
                        "      \"code\": 4,\n" +
                        "      \"description\": \"Account number format is incorrect\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class));
        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"00000012\"\n" +
                        "}", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": false,\n" +
                        "    \"iban\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"error\",\n" +
                        "      \"code\": 4,\n" +
                        "      \"description\": \"Account number format is incorrect\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class));
        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"12345673\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": false,\n" +
                        "    \"iban\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"error\",\n" +
                        "      \"code\": 7,\n" +
                        "      \"description\": \"Modulus check has failed\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class));

        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"123 45672\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB57BIBA00000312345672\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 1,\n" +
                        "      \"description\": \"Account details were not in standard form and have been transposed\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class));


        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000002\",\n" +
                        "  \"accountNumber\":\"12345678\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB35BRNU00000212345678\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n", ValidationResultWrapper.class));


        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"22345616\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB02BIBA00000322345616\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 5,\n" +
                        "      \"description\": \"Account does not support Direct Debit transactions\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n", ValidationResultWrapper.class));


        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"22345632\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB55BIBA00000322345632\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 7,\n" +
                        "      \"description\": \"Account does not support Direct Credit transactions\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n", ValidationResultWrapper.class));

        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"22345624\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB77BIBA00000322345624\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 65,\n" +
                        "      \"description\": \"Collection account requires a reference or roll account number\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n", ValidationResultWrapper.class));

        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000003\",\n" +
                        "  \"accountNumber\":\"22345683\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB36BIBA00000322345683\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 78,\n" +
                        "      \"description\": \"Account does not support AUDDIS transactions\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n", ValidationResultWrapper.class));


        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000004\",\n" +
                        "  \"accountNumber\":\"22345610\"\n" +
                        "}\n", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB04BRNU00000422345610\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"information\",\n" +
                        "      \"code\": 1,\n" +
                        "      \"description\": \"Alternate information is available for this account\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class));

        validationErrors.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"000004\",\n" +
                        "  \"accountNumber\":\"00000123\"\n" +
                        "}", SILBankDetails.class),
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": false,\n" +
                        "    \"iban\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"error\",\n" +
                        "      \"code\": 3,\n" +
                        "      \"description\": \"\"" +
                        "    }\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class));
        return validationErrors;
    }

    private static Map<AccountDetails, VerificationResultWrapper> buildVerificationResults() {
        Map<AccountDetails, VerificationResultWrapper> verificationResults = new HashMap<>();
        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51406795\",\n" +
                        "  \"companyName\": \"Vitruvius Stonework Limited\",\n" +
                        "  \"registrationNumber\": \"60674010\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"1\", \"street\":\"Riff Street\",\"locality\":\"\",\"town\":\"Bath\",\"postcode\": \"BA1 5LR\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": 1,\n" +
                        "    \"addressScore\": 1,\n" +
                        "    \"companyNameScore\": 9,\n" +
                        "    \"regNumberScore\": \"Match\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51406795\",\n" +
                        "  \"companyName\": \"Vitruvius Stonework Limited\",\n" +
                        "  \"registrationNumber\": \"60674010\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Springbank Chapel Green\", \"street\":\"Charlmont Road\",\"locality\":\"\",\"town\":\"London\",\"postcode\": \"SW17 9AB\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": 1,\n" +
                        "    \"addressScore\": 8,\n" +
                        "    \"companyNameScore\": 9,\n" +
                        "    \"regNumberScore\": \"Match\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404749\",\n" +
                        "  \"accountNumber\":\"70008818\",\n" +
                        "  \"companyName\": \"A B Cad Services\",\n" +
                        "  \"registrationNumber\": \"06624503\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Woodhouse Farm\", \"street\":\"Abbots Close\",\"locality\":\"\",\"town\":\"Somerset\",\"postcode\": \"TA19 0EF\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": 1,\n" +
                        "    \"addressScore\": 9,\n" +
                        "    \"companyNameScore\": 9,\n" +
                        "    \"regNumberScore\": \"Match\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404749\",\n" +
                        "  \"accountNumber\":\"60016136\",\n" +
                        "  \"companyName\": \"Armstrong & Butler Ltd\",\n" +
                        "  \"registrationNumber\": \"57012850\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Woodhouse Farm\", \"street\":\"Admaston Road\",\"locality\":\"\",\"town\":\"London\",\"postcode\": \"SE18 2TF\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": 1,\n" +
                        "    \"addressScore\": 9,\n" +
                        "    \"companyNameScore\": 9,\n" +
                        "    \"regNumberScore\": \"Match\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)

        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51431439\",\n" +
                        "  \"companyName\": \"UTEK Europe Limited\",\n" +
                        "  \"registrationNumber\": \"13945420\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Marlborough House Westminster\", \"street\":\"Andrews Walk\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"SE17 3JQ\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": null,\n" +
                        "    \"addressScore\": null,\n" +
                        "    \"companyNameScore\": null,\n" +
                        "    \"regNumberScore\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51436651\",\n" +
                        "  \"companyName\": \"Mark Shenton\",\n" +
                        "  \"registrationNumber\": \"06624523\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Marlborough House Westminster\", \"street\":\"Copper Mill Lane\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"SW17 0BN\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": null,\n" +
                        "    \"addressScore\": null,\n" +
                        "    \"companyNameScore\": null,\n" +
                        "    \"regNumberScore\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404750\",\n" +
                        "  \"accountNumber\":\"70004391\",\n" +
                        "  \"companyName\": \"Blue Moon Marketing Systems Ltd\",\n" +
                        "  \"registrationNumber\": \"63074650\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Poppleton Community Sports Pav\", \"street\":\"Lancaster Gardens\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"BR1 2ED\"}\n" +
                        "}\n", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": null,\n" +
                        "    \"addressScore\": null,\n" +
                        "    \"companyNameScore\": null,\n" +
                        "    \"regNumberScore\": null,\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51440000\",\n" +
                        "  \"companyName\": \"\",\n" +
                        "  \"registrationNumber\": \"\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"\", \"street\":\"\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"\"}\n" +
                        "}\n", AccountDetails.class), null);

        verificationResults.put(
                new AccountDetails("090127", "78132557", "Consumed By Riffage Ltd", "06477798",
                        new Address(null, "1", "Riff Street", null, "Bath", "BA1 5LR")
                ),
                new VerificationResultWrapper(new VerificationResult("9", "8", "4", "No Match", emptyList()))
        );

        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51406795\",\n" +
                        "  \"companyName\": \"Vitruvius Stonework Limited\",\n" +
                        "  \"registrationNumber\": \"60674010\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Montrose House 1\", \"street\":\"Clayhill Park\",\"locality\":\"Cheshire West and Chester\",\"town\":\"Neston\",\"postcode\": \"CH64 3RU\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"VerificationResult\": {\n" +
                        "    \"personalDetailsScore\": 1,\n" +
                        "    \"addressScore\": 5,\n" +
                        "    \"companyNameScore\": 9,\n" +
                        "    \"regNumberScore\": \"Match\",\n" +
                        "    \"conditions\": {\n" +
                        "      \"severity\": \"warning\",\n" +
                        "      \"code\": 2,\n" +
                        "      \"description\": \"Modulus check algorithm is unavailable for these account details\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", VerificationResultWrapper.class)
        );
        return verificationResults;
    }

    private static Map<AccountDetails, SilExperianError> buildOtherErrorsDuringVerification() {
        Map<AccountDetails, SilExperianError> otherErrorsDuringVerification = new HashMap<>();
        otherErrorsDuringVerification.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51440000\",\n" +
                        "  \"companyName\": \"\",\n" +
                        "  \"registrationNumber\": \"\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"\", \"street\":\"\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"\"}\n" +
                        "}", AccountDetails.class),
                fromJson("{\n" +
                        "  \"code\": \"400\",\n" +
                        "  \"type\": \"Status report\",\n" +
                        "  \"message\": \"Invalid Parameter\",\n" +
                        "  \"description\": \"Invalid Parameter\"\n" +
                        "}", SilExperianError.class)
        );
        return otherErrorsDuringVerification;
    }

}
