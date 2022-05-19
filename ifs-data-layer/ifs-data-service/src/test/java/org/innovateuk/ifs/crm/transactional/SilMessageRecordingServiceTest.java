package org.innovateuk.ifs.crm.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.activitylog.repository.SilMessageRepository;
import org.innovateuk.ifs.sil.SilPayloadKeyType;
import org.innovateuk.ifs.sil.SilPayloadType;
import org.innovateuk.ifs.sil.crm.resource.SilMessage;
import org.innovateuk.ifs.util.TimeMachine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.verify;

/**
 * Tests around the {@link SilMessageRecordingService}.
 */
public class SilMessageRecordingServiceTest extends BaseServiceUnitTest<SilMessageRecordingServiceImpl> {


    @Before
    public void setup() {
        ZonedDateTime fixedClock = ZonedDateTime.parse("2021-10-12T00:00:00.0Z");
        TimeMachine.useFixedClockAt(fixedClock);
    }

    private static final String USER_ID = "1007";
    private static final String APPLICATION_ID = "2005";
    private static final String COMPETITION_ID = "4006";
    @Mock
    SilMessageRepository silMessageRepository;


    @Test
    public void syncCrmContact() {
        String silContactJson = "{\n" +
                "  \"email\": \"test2606_10@gmail.com\",\n" +
                "  \"lastName\": \"Samudrala2606_10\",\n" +
                "  \"firstName\": \"Suruchi\",\n" +
                "  \"title\": \"Mrs\",\n" +
                "  \"jobTitle\": \"Lead Dev\",\n" +
                "  \"sourceSystem\": \"_connect\",\n" +
                "  \"srcSysContactId\": \"Test2606_10\",\n" +
                "  \"Address\":\n" +
                "\n" +
                "{     \"buildingName\": \"5, Polonez Court\",     \"Street\": \"\"   }\n" +
                ",\n" +
                "  \"ifsUuid\": \"3036e2d4-dbe1-4d0b-80a2-da841dd1f42c\",\n" +
                "  \"fundingType\": \"Loan\",\n" +
                "  \"ifsAppID\": \"10000051\",\n" +
                "  \"organisation\":\n" +
                "\n" +
                "{   \"name\": \"Test0605_10rg\",   \"registrationNumber\": \"Test0605_1\",   \"srcSysOrgId\": \"Test2605_20rg\"   }\n" +
                "}";
        service.recordSilMessage(SilPayloadType.CONTACT, SilPayloadKeyType.USER_ID, USER_ID,
                silContactJson, HttpStatus.ACCEPTED);


        SilMessage silMessage = SilMessage.builder().payloadType(SilPayloadType.CONTACT).keyType(SilPayloadKeyType.USER_ID)
                .keyValue(USER_ID).payload(silContactJson).responseCode(HttpStatus.ACCEPTED.name()).dateCreated(TimeMachine.now()).build();

        verify(silMessageRepository).save(silMessage);
    }

    @Test
    public void lineDraw() {
        String silLineDrawPayload = "{\n" +
                "   \"competitionID\": 12345,\n" +
                "    applications:\n" +
                "    [\n" +
                "        {\n" +
                "            \"appID\": 10000234,\n" +
                "            \"scoreAverage\": 76\n" +
                "            \"scoreSpread\": 3\n" +
                "            \"assessorNumber\": 5\n" +
                "            \"assessorNotInScope\": 2\n" +
                "            \"assessorRecommended\": 4\n" +
                "            \"assessorNotRecommended\": 1\n" +
                "        }\n" +
                "        {\n" +
                "            \"appID\": 10000245,\n" +
                "            \"scoreAverage\": 57\n" +
                "            \"scoreSpread\": 6\n" +
                "            \"assessorNumber\": 5\n" +
                "            \"assessorNotInScope\": 1\n" +
                "            \"assessorRecommended\": 3\n" +
                "            \"assessorNotRecommended\": 2\n" +
                "        }\n" +
                "    ]\n" +
                "}" +
                "}";
        service.recordSilMessage(SilPayloadType.ASSESSMENT_COMPLETE, SilPayloadKeyType.COMPETITION_ID, COMPETITION_ID,
                silLineDrawPayload, HttpStatus.ACCEPTED);


        SilMessage silMessage = SilMessage.builder().payloadType(SilPayloadType.ASSESSMENT_COMPLETE).keyType(SilPayloadKeyType.COMPETITION_ID)
                .keyValue(COMPETITION_ID).payload(silLineDrawPayload).responseCode(HttpStatus.ACCEPTED.name()).dateCreated(TimeMachine.now()).build();

        verify(silMessageRepository).save(silMessage);
    }

    @Test
    public void loansSubmission() {
        String silLoansSubmission = "{\n" +
                "  \"appID\" : 10033417,\n" +
                "  \"appSubDate\" : \"2022-04-21T18:29:29.671Z\",\n" +
                "  \"appName\" : \"Phoenix Mercury\",\n" +
                "  \"appLoc\" : \"CV346NJ\",\n" +
                "  \"compCode\" : \"2203-3\",\n" +
                "  \"compName\" : \"Innovation Loans Future Economy Competition – Round 1\",\n" +
                "  \"projectDuration\" : 22,\n" +
                "  \"projTotalCost\" : 1867468.0,\n" +
                "  \"projOtherFunding\" : 0.0,\n" +
                "  \"markedIneligible\" : null,\n" +
                "  \"eligibilityStatusChangeDate\" : null,\n" +
                "  \"eligibilityStatusChangeSource\" : null\n" +
                "}";
        service.recordSilMessage(SilPayloadType.APPLICATION_SUBMISSION, SilPayloadKeyType.APPLICATION_ID, APPLICATION_ID,
                silLoansSubmission, HttpStatus.ACCEPTED);


        SilMessage silMessage = SilMessage.builder().payloadType(SilPayloadType.APPLICATION_SUBMISSION).keyType(SilPayloadKeyType.APPLICATION_ID)
                .keyValue(APPLICATION_ID).payload(silLoansSubmission).responseCode(HttpStatus.ACCEPTED.name()).dateCreated(TimeMachine.now()).build();

        verify(silMessageRepository).save(silMessage);
    }

    @Test
    public void applicationUpdate() {
        String silLoansSubmission = "{\n" +
                "   \"questionSetupType\":\"LOAN_BUSINESS_AND_FINANCIAL_INFORMATION\",\n" +
                "   \"completionStatus\":\"Complete\",\n" +
                "   \"completionDate\":\"2022-04-11T12:15:45.000Z\"\n" +
                "}";
        service.recordSilMessage(SilPayloadType.APPLICATION_UPDATE, SilPayloadKeyType.APPLICATION_ID, APPLICATION_ID,
                silLoansSubmission, null);


        SilMessage silMessage = SilMessage.builder().payloadType(SilPayloadType.APPLICATION_UPDATE).keyType(SilPayloadKeyType.APPLICATION_ID)
                .keyValue(APPLICATION_ID).payload(silLoansSubmission).responseCode(null).dateCreated(TimeMachine.now()).build();

        verify(silMessageRepository).save(silMessage);
    }

    @Test
    public void ediUpdate() {
        String silLoansSubmission = "{\n" +
                "   \"ediStatus\":\"In Progress\",\n" +
                "   \"ediReviewDate\":\"2020-01-31T01:02:03Z\"\n" +
                "}";
        service.recordSilMessage(SilPayloadType.USER_UPDATE, SilPayloadKeyType.USER_ID, USER_ID,
                silLoansSubmission, null);


        SilMessage silMessage = SilMessage.builder().payloadType(SilPayloadType.USER_UPDATE).keyType(SilPayloadKeyType.USER_ID)
                .keyValue(USER_ID).payload(silLoansSubmission).responseCode(null).dateCreated(TimeMachine.now()).build();

        verify(silMessageRepository).save(silMessage);
    }


    @Override
    protected SilMessageRecordingServiceImpl supplyServiceUnderTest() {
        return new SilMessageRecordingServiceImpl();
    }


}