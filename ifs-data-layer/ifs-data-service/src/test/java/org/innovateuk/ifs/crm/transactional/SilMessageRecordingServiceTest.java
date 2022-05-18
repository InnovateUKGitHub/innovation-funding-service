package org.innovateuk.ifs.crm.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.activitylog.repository.SilMessageRepository;
import org.innovateuk.ifs.sil.SIlPayloadKeyType;
import org.innovateuk.ifs.sil.SIlPayloadType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

/**
 * Tests around the {@link SILMessageRecordingService}.
 */
public class SilMessageRecordingServiceTest extends BaseServiceUnitTest<SILMessageRecordingServiceImpl> {



    private static final String UUDI = "a63ddc28-925e-4b1b-9513-bdab2e69bbfd";
    @Mock
    SilMessageRepository silMessageRepository;

    @Override
    protected SILMessageRecordingServiceImpl supplyServiceUnderTest() {
        return new SILMessageRecordingServiceImpl();
    }


    @Test
    public void syncExternalCrmContact() {

        String silContactJson = "{   \"ifsUuid\" : \"a4099fef-b217-44b7-84d3-6821db57798e\",   \"experienceType\" : \"Loan\",   \"ifsAppID\" : \"376\",   \"email\" : \"steve.smith@empire.com\",   \"lastName\" : \"Smith\",   \"firstName\" : \"Steve\",   \"title\" : null,   \"jobTitle\" : null,   \"organisation\" : {     \"name\" : \"Empire Ltd\",     \"registrationNumber\" : \"60674010\",     \"registeredAddress\" : {       \"buildingName\" : \"1\",       \"street\" : \"Empire Road\",       \"locality\" : \"South Yorkshire\",       \"town\" : \"Sheffield\",       \"postcode\" : \"S1 2ED\",       \"country\" : \"\"     },     \"srcSysOrgId\" : \"21\"   },   \"sourceSystem\" : \"IFS\",   \"srcSysContactId\" : \"70\",   \"phoneNumber\" : \"46439359578\",   \"Address\" : null }";
        service.recordSilMessage(SIlPayloadType.CONTACT, SIlPayloadKeyType.USER_ID, UUDI,
                silContactJson, HttpStatus.ACCEPTED);

    }


}