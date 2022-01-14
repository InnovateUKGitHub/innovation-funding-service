package org.innovateuk.ifs.sil.email.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;

/**
 * A REST-based implementation of the SIL email endpoint
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

            final Either<ResponseEntity<Void>, ResponseEntity<Void>> response =
                    adaptor.restPostWithEntity(silRestServiceUrl + silSendmailPath, message, Void.class, Void.class, HttpStatus.ACCEPTED);

            return response.mapLeftOrRight(
                    failure -> serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, failure.getStatusCode())),
                    success -> serviceSuccess(message));
        });
    }
}
