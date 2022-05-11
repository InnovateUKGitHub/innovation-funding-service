package org.innovateuk.ifs.sil.crm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.activitylog.transactional.SILMessagingService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.SIlPayloadKeyType;
import org.innovateuk.ifs.sil.SIlPayloadType;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilCrmError;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_NOT_UPDATED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CONTACT_NOT_UPDATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;

@Component
@Slf4j
public class RestSilCrmEndpoint implements SilCrmEndpoint {

    protected static ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.baseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.crmContacts}")
    private String silCrmContacts;

    @Value("${sil.rest.crmApplications}")
    private String silCrmApplications;

    @Value("${sil.rest.crmDecisionmatrix}")
    private String silmDecisionmatrix;

    @Autowired
    SILMessagingService silMessagingService;

    @SneakyThrows(JsonProcessingException.class)
    @Override
    public ServiceResult<Void> updateContact(SilContact silContact) {
        String silContactJson = objectWriter.writeValueAsString(silContact);
        log.info("Json Payload: " + silContactJson);
        return handlingErrors(() -> {
                    final Either<ResponseEntity<SilCrmError>, ResponseEntity<Void>> response = adaptor.restPostWithEntity(silRestServiceUrl + silCrmContacts, silContact, Void.class, SilCrmError.class, HttpStatus.ACCEPTED);
                    return response.mapLeftOrRight(failure -> {
                                log.error("Error updating SIL contact " + silContact);
                                silMessagingService.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID, silContact.getIfsUuid(),
                                        silContactJson, failure.getStatusCode());
                                return serviceFailure(new Error(CONTACT_NOT_UPDATED));
                            },

                            success -> {
                                silMessagingService.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID,
                                        silContact.getIfsUuid(), silContactJson, HttpStatus.ACCEPTED);
                                return serviceSuccess();
                            });
                }
        );
    }


    @SneakyThrows(JsonProcessingException.class)
    @Override
    public ServiceResult<Void> updateLoanApplicationState(SilLoanApplication silApplication) {
        String silApplicationJson = objectWriter.writeValueAsString(silApplication);
        log.info("Json Payload: " + silApplicationJson);
        return handlingErrors(() -> {
                    final Either<ResponseEntity<SilCrmError>, ResponseEntity<Void>> response = adaptor.restPostWithEntity(silRestServiceUrl + silCrmApplications, silApplicationJson, Void.class, SilCrmError.class, HttpStatus.ACCEPTED);
                    return response.mapLeftOrRight(failure -> {
                                log.error("Error updating SIL Loan Application state: " + silApplication +
                                        "Error: " + failure);
                                silMessagingService.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID, Integer.toString(silApplication.getApplicationID()),
                                        silApplicationJson, failure.getStatusCode());
                                return serviceFailure(new Error(APPLICATION_NOT_UPDATED));
                            },
                            success -> {
                                silMessagingService.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID, Integer.toString(silApplication.getApplicationID()),
                                        silApplicationJson, HttpStatus.ACCEPTED);
                                return serviceSuccess();
                            });
                }
        );
    }

    @Override
    @SneakyThrows(JsonProcessingException.class)
    public ServiceResult<Void> updateLoanAssessment(SilLoanAssessment silLoanAssessment) {
        String silApplicationJson = objectWriter.writeValueAsString(silLoanAssessment);
        log.info("Json Payload: " + silApplicationJson);
        return handlingErrors(() -> {
                    final Either<ResponseEntity<SilCrmError>, ResponseEntity<Void>> response = adaptor.restPostWithEntity(silRestServiceUrl + silmDecisionmatrix, silApplicationJson, Void.class, SilCrmError.class, HttpStatus.ACCEPTED);
                    return response.mapLeftOrRight(failure -> {
                                log.error("Error updating SIL Loan Assessment: " + silLoanAssessment +
                                        "Error: " + failure);
                                silMessagingService.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID, Long.toString(silLoanAssessment.getCompetitionID()),
                                        silApplicationJson, failure.getStatusCode());
                                return serviceFailure(new Error(APPLICATION_NOT_UPDATED));
                            },
                            success -> {
                                silMessagingService.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID, Long.toString(silLoanAssessment.getCompetitionID()),
                                        silApplicationJson, HttpStatus.ACCEPTED);
                                return serviceSuccess();
                            });
                }
        );
    }

}