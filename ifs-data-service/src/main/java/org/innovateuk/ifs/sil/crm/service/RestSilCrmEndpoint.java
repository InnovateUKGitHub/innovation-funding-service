package org.innovateuk.ifs.sil.crm.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilCrmError;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CONTACT_NOT_UPDATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;

@Component
public class RestSilCrmEndpoint implements SilCrmEndpoint {

    private static final Log LOG = LogFactory.getLog(RestSilCrmEndpoint.class);

    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.crmBaseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.crmContacts}")
    private String silCrmContacts;

    @Override
    public ServiceResult<Void> updateContact(SilContact silContact) {
        return handlingErrors(() -> {
                    final Either<ResponseEntity<SilCrmError>, ResponseEntity<Void>> response = adaptor.restPostWithEntity(silRestServiceUrl + silCrmContacts, silContact, Void.class, SilCrmError.class, HttpStatus.ACCEPTED);
                    return response.mapLeftOrRight(failure -> {
                                LOG.error("Error updating SIL contact " + silContact);
                                return serviceFailure(new Error(CONTACT_NOT_UPDATED));
                            },
                            success -> serviceSuccess());
                }
        );
    }
}
