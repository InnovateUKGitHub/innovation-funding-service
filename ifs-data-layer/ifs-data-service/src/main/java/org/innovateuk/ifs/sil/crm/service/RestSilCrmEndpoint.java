package org.innovateuk.ifs.sil.crm.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.crm.resource.SilApplication;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilCrmError;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;

@Component
public class RestSilCrmEndpoint implements SilCrmEndpoint {

    private static final Log LOG = LogFactory.getLog(RestSilCrmEndpoint.class);

    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.baseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.crmContacts}")
    private String silCrmContacts;

    @Value("${sil.rest.crmApplications}")
    private String silCrmApplications;

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

    @Override
    public ServiceResult<Void> updateApplicationEligibility(SilApplication silApplication) {
        return handlingErrors(() -> {
                    final Either<ResponseEntity<SilCrmError>, ResponseEntity<Void>> response = adaptor.restPostWithEntity(silRestServiceUrl + silCrmApplications, silApplication, Void.class, SilCrmError.class, HttpStatus.OK);
                    return response.mapLeftOrRight(failure -> {
                                LOG.error("Error updating SIL application eligibility: " + silApplication);
                                return serviceFailure(new Error(APPLICATION_NOT_UPDATED));
                            },
                            success -> serviceSuccess());
                }
        );
    }

}