package com.worth.ifs.sil.experian.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.AbstractRestTemplateAdaptor;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.sil.experian.resource.*;
import com.worth.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.error.CommonFailureKeys.EXPERIAN_VALIDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.EXPERIAN_VERIFICATION_FAILED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A simple logging implementation of the SIL email endpoint as opposed to a REST-based endpoint
 */
@Component
public class RestSilExperianEndpoint implements SilExperianEndpoint {

    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.baseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.experianValidate}")
    private String silExperianValidate;

    @Value("${sil.rest.experianVerify}")
    private String silExperianVerify;

    @Override
    public ServiceResult<ValidationResult> validate(SILBankDetails bankDetails) {
        final Either<ResponseEntity<SilError>, ResponseEntity<ValidationResultWrapper>> response = adaptor.restPostWithEntity(silRestServiceUrl + silExperianValidate, bankDetails, ValidationResultWrapper.class, SilError.class, HttpStatus.OK, HttpStatus.ACCEPTED);
        if(response.isLeft()){
            return serviceFailure(new Error(EXPERIAN_VALIDATION_FAILED));
        } else {
            return serviceSuccess(response.getRight().getBody().getValidationResult());
        }
    }

    @Override
    public ServiceResult<VerificationResult> verify(AccountDetails accountDetails) {
        final Either<ResponseEntity<SilError>, ResponseEntity<VerificationResultWrapper>> response = adaptor.restPostWithEntity(silRestServiceUrl + silExperianVerify, accountDetails, VerificationResultWrapper.class, SilError.class, HttpStatus.OK, HttpStatus.ACCEPTED);
        if(response.isLeft()){
            return serviceFailure(new Error(EXPERIAN_VERIFICATION_FAILED));
        } else {
            return serviceSuccess(response.getRight().getBody().getVerificationResult());
        }
    }
}
