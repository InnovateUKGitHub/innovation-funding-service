package com.worth.ifs.sil.email.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.AbstractRestTemplateAdaptor;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.service.ServiceResult.*;

/**
 * A simple logging implementation of the SIL email endpoint as opposed to a REST-based endpoint
 */
@Component
public class RestSilEmailEndpoint implements SilEmailEndpoint {

    private static final Log LOG = LogFactory.getLog(RestSilEmailEndpoint.class);

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
