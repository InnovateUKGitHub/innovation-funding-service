package org.innovateuk.ifs.sil.experian.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.experian.resource.*;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EXPERIAN_VALIDATION_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EXPERIAN_VERIFICATION_FAILED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

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
        final Either<ResponseEntity<SilExperianError>, ResponseEntity<ValidationResultWrapper>> response = adaptor.restPostWithEntity(silRestServiceUrl + silExperianValidate, bankDetails, ValidationResultWrapper.class, SilExperianError.class, HttpStatus.OK, HttpStatus.ACCEPTED);
        if(response.isLeft()){
            return serviceFailure(new Error(EXPERIAN_VALIDATION_FAILED));
        } else {
            return serviceSuccess(response.getRight().getBody().getValidationResult());
        }
    }

    @Override
    public ServiceResult<VerificationResult> verify(AccountDetails accountDetails) {
        final Either<ResponseEntity<SilExperianError>, ResponseEntity<VerificationResultWrapper>> response = adaptor.restPostWithEntity(silRestServiceUrl + silExperianVerify, accountDetails, VerificationResultWrapper.class, SilExperianError.class, HttpStatus.OK, HttpStatus.ACCEPTED);
        if(response.isLeft()){
            return serviceFailure(new Error(EXPERIAN_VERIFICATION_FAILED));
        } else {
            return serviceSuccess(response.getRight().getBody().getVerificationResult());
        }
    }
}
