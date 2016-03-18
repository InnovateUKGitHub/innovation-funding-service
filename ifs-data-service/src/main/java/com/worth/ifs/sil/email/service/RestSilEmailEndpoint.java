package com.worth.ifs.sil.email.service;

import static com.worth.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.service.ServiceResult.handlingErrors;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.AbstractRestTemplateAdaptor;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.util.Either;

/**
 * A simple logging implementation of the SIL email endpoint as opposed to a REST-based endpoint
 */
@Component
public class RestSilEmailEndpoint implements SilEmailEndpoint {

    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.baseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.sendmail}")
    private String silSendmailPath;

    @Override
    public ServiceResult<SilEmailMessage> sendEmail(SilEmailMessage message) {
        return handlingErrors(() -> {
                    final Either<ResponseEntity<Void>, ResponseEntity<Void>> response = adaptor.restPostWithEntity(silRestServiceUrl + silSendmailPath, message, Void.class, Void.class, HttpStatus.ACCEPTED);
                    return response.mapLeftOrRight(failure -> serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE)),
                            success -> serviceSuccess(message));
                }
        );
    }
}
