package com.worth.ifs.sil.experian.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.sil.experian.resource.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.util.JsonMappingUtil.fromJson;
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
    public static HashMap<SILBankDetails, ValidationResultWrapper> validationErrors;
    public static HashMap<SILBankDetails, SilError> otherErrorsDuringValidation;
    public static ValidationResultWrapper defaultValidationResult;
    public static HashMap<AccountDetails, VerificationResultWrapper> verificationResults;
    public static HashMap<AccountDetails, SilError> otherErrorsDuringVerification;
    public static VerificationResultWrapper defaultVerificationResult;

    static {
        buildValidationTestData();
        buildVerficiationTestData();
    }

    @RequestMapping(value = "/experianValidate", method = POST)
    public RestResult<Object> experianValidate(@RequestBody SILBankDetails bankDetails) {
        LOG.info("Stubbing out SIL experian validation: " + bankDetails);
        ValidationResultWrapper validationResultWrapper = validationErrors.get(bankDetails);
        if (validationResultWrapper == null) {
            SilError silError = otherErrorsDuringValidation.get(bankDetails);
            if(silError == null) {
                validationResultWrapper = defaultValidationResult;
            } else {
                return restSuccess(silError);
            }
        }
        return restSuccess(validationResultWrapper);
    }

    @RequestMapping(value = "/experianVerify", method = POST)
    public RestResult<Object> experianVerify(@RequestBody AccountDetails accountDetails) {
        VerificationResultWrapper verificationResultWrapper = verificationResults.get(accountDetails);
        if (verificationResultWrapper == null) {
            SilError silError = otherErrorsDuringVerification.get(accountDetails);
            if(silError == null) {
                verificationResultWrapper = defaultVerificationResult;
            } else {
                return restSuccess(silError);
            }
        }
        return restSuccess(verificationResultWrapper);
    }

    private static void buildValidationTestData() {
        validationErrors = new HashMap<>();
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


        defaultValidationResult =
                fromJson("{\n" +
                        "  \"ValidationResult\": {\n" +
                        "    \"checkPassed\": true,\n" +
                        "    \"iban\": \"GB53BRNU00000412345677\"\n" +
                        "  }\n" +
                        "}", ValidationResultWrapper.class);

        otherErrorsDuringValidation = new HashMap<>();
    }

    private static void buildVerficiationTestData() {
        verificationResults = new HashMap<>();
        verificationResults.put(
                fromJson("{\n" +
                        "  \"sortcode\":\"404745\",\n" +
                        "  \"accountNumber\":\"51406795\",\n" +
                        "  \"companyName\": \"Vitruvius Stonework Limited\",\n" +
                        "  \"registrationNumber\": \"60674010\",\n" +
                        "  \"firstName\": \"NA\",\n" +
                        "  \"lastName\": \"NA\",\n" +
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Springbank Chapel Green\", \"street\":\"Charlmont Road\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"SW17 9AB\"}\n" +
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
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Woodhouse Farm\", \"street\":\"Abbots Close\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"TA19 0EF\"}\n" +
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
                        "  \"address\": {\"organisation\":\"\", \"buildingName\":\"Woodhouse Farm\", \"street\":\"Admaston Road\",\"locality\":\"\",\"town\":\"\",\"postcode\": \"SE18 2TF\"}\n" +
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
                new VerificationResultWrapper(new VerificationResult("9", "8", "6", "No Match", emptyList()))
        );

        otherErrorsDuringVerification = new HashMap<>();
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
                        "}", SilError.class)
        );
        defaultVerificationResult = new VerificationResultWrapper(new VerificationResult("7", "7", "7", "Match", singletonList(new Condition("warning", 2, "Modulus check algorithm is unavailable for these account details"))));
    }
}