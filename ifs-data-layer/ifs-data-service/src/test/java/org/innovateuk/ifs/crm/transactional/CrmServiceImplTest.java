package org.innovateuk.ifs.crm.transactional;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.ApplicationSummarisationService;
import org.innovateuk.ifs.assessment.builder.AssessmentBuilder;
import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseServiceImpl;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.log.MemoryAppender;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationAddressService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationExecutiveOfficerResourceBuilder.newOrganisationExecutiveOfficerResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationSicCodeResourceBuilder.newOrganisationSicCodeResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests around the {@link CrmServiceImpl}.
 */
public class CrmServiceImplTest extends BaseServiceUnitTest<CrmServiceImpl> {

    @Mock
    private BaseUserService baseUserService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationSummarisationService applicationSummarisationService;
    @Mock
    private ApplicationAssessmentService applicationAssessmentService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private OrganisationAddressService organisationAddressService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseServiceMock;

    @Mock
    private SilCrmEndpoint silCrmEndpoint;

    private static MemoryAppender memoryAppender;
    private static final String LOGGER_NAME = "org.innovateuk.ifs.crm.transactional";

    @Mock
    private CompetitionService competitionServiceMock;
    @Mock
    private ApplicationService applicationService;
    @Mock
    private AssessorFormInputResponseRepository assessorFormInputResponseRepositoryMock;

    @InjectMocks
    private AssessorFormInputResponseService assessorFormInputResponseService = new AssessorFormInputResponseServiceImpl();

    @Before
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);
        memoryAppender.start();

        //tell tests to return the specified LOCAL_DATE when calling ZonedDateTime.now(clock)
        ZonedDateTime fixedClock = ZonedDateTime.parse("2021-10-12T09:38:12.850Z");
        TimeMachine.useFixedClockAt(fixedClock);

        ReflectionTestUtils.setField(service, "eligibilityStatusChangeSource", "IFS");
        ReflectionTestUtils.setField(service, "isLoanPartBEnabled", true);
        //MockitoAnnotations.initMocks(this);
    }

    @After
    public void cleanUp() {
        memoryAppender.reset();
        memoryAppender.stop();
    }

    @Override
    protected CrmServiceImpl supplyServiceUnderTest() {
        CrmServiceImpl service = new CrmServiceImpl();
        ReflectionTestUtils.setField(service, "newOrganisationSearchEnabled", false);
        return service;
    }


    @Test
    public void syncExternalCrmContact() {
        long userId = 1L;
        UserResource user = newUserResource().withRoleGlobal(APPLICANT).build();

        List<OrganisationResource> organisations = newOrganisationResource().withCompaniesHouseNumber("Something", "Else").build(2);

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(organisations));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());


        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(0))));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(1))));
    }


    @Test
    public void syncExternalCrmContactWithOrganisationUpdates() {
        long userId = 1L;

        UserResource user = newUserResource()
                .withRoleGlobal(APPLICANT)
                .build();

        OrganisationResource organisation = newOrganisationResource()
                .withDateOfIncorporation(LocalDate.now())
                .withSicCodes(newOrganisationSicCodeResource().withSicCode("code-1", "code-2").build(2))
                .withExecutiveOfficers(newOrganisationExecutiveOfficerResource().withName("director-1", "director-2").build(2))
                .build();

        AddressType addressType = newAddressType()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();

        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddress(newAddressResource()
                        .withAddressLine1("Line1")
                        .withAddressLine2("Line2")
                        .withAddressLine3("Line3")
                        .withCounty("County")
                        .withTown("Town")
                        .withCountry("Country")
                        .withPostcode("Postcode").build())
                .withAddressType(newAddressTypeResource()
                        .withId(OrganisationAddressType.REGISTERED.getId())
                        .withName(OrganisationAddressType.REGISTERED.name()).build())
                .build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(Collections.singletonList(organisation)));
        when(organisationAddressService.findByOrganisationIdAndAddressType(organisation.getId(), addressType))
                .thenReturn(serviceSuccess(Collections.singletonList(organisationAddressResource)));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ReflectionTestUtils.setField(service, "newOrganisationSearchEnabled", true);
        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContactWithOrganisationUpdates(user, organisation)));
    }

    @Test
    public void syncExternalCrmContactForProject() {
        long userId = 1L;
        long projectId = 2L;

        UserResource user = newUserResource().withRoleGlobal(APPLICANT).build();

        OrganisationResource organisation = newOrganisationResource()
                .withCompaniesHouseNumber("Something", "Else")
                .build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getByUserAndProjectId(userId, projectId)).thenReturn(serviceSuccess(organisation));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId, projectId);

        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisation)));
    }

    @Test
    public void syncExternalCrmContactForProjectWithOrganisationUpdates() {
        long userId = 1L;
        long projectId = 2L;

        UserResource user = newUserResource()
                .withRoleGlobal(APPLICANT)
                .build();

        OrganisationResource organisation = newOrganisationResource()
                .withDateOfIncorporation(LocalDate.now())
                .withSicCodes(newOrganisationSicCodeResource().withSicCode("code-1", "code-2").build(2))
                .withExecutiveOfficers(newOrganisationExecutiveOfficerResource().withName("director-1", "director-2").build(2))
                .build();

        AddressType addressType = newAddressType()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();

        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddress(newAddressResource()
                        .withAddressLine1("Line1")
                        .withAddressLine2("Line2")
                        .withAddressLine3("Line3")
                        .withCounty("County")
                        .withTown("Town")
                        .withCountry("Country")
                        .withPostcode("Postcode").build())
                .withAddressType(newAddressTypeResource()
                        .withId(OrganisationAddressType.REGISTERED.getId())
                        .withName(OrganisationAddressType.REGISTERED.name()).build())
                .build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getByUserAndProjectId(userId, projectId)).thenReturn(serviceSuccess(organisation));
        when(organisationAddressService.findByOrganisationIdAndAddressType(organisation.getId(), addressType))
                .thenReturn(serviceSuccess(Collections.singletonList(organisationAddressResource)));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ReflectionTestUtils.setField(service, "newOrganisationSearchEnabled", true);
        ServiceResult<Void> result = service.syncCrmContact(userId, projectId);

        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContactWithOrganisationUpdates(user, organisation)));
    }

    @Test
    public void syncExternalCrmContactWithExperienceTypeLOANShouldHaveAllAttributes() {

        String expectedLogMessage = "Updating CRM contact test@innovate.com and organisation OrganisationResource 6 \n" +
                "Payload is:SilContact(ifsUuid=17a0e34c-719a-4db4-b011-ccd4c375ad79, experienceType=Loan, ifsAppID=3, email=test@innovate.com, lastName=Doe, firstName=Jon, title=null, jobTitle=null, " +
                "address=null, organisation=SilOrganisation(name=OrganisationResource 6, registrationNumber=null, registeredAddress=SilAddress(buildingName=Line1, " +
                "street=Line2, Line3, locality=County, town=Town, postcode=Postcode, country=Country), srcSysOrgId=6), sourceSystem=IFS, srcSysContactId=1) ";


        long userId = 1L;
        long applicationId = 3L;
        long competitionId = 4L;
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.LOAN);


        UserResource user = newUserResource()
                .withRoleGlobal(APPLICANT)
                .withId(1L)
                .withEmail("test@innovate.com")
                .withFirstName("Jon")
                .withLastName("Doe")
                .withUid("17a0e34c-719a-4db4-b011-ccd4c375ad79")
                .build();

        List<OrganisationResource> organisation = Arrays.asList(newOrganisationResource()
                .withDateOfIncorporation(LocalDate.now())
                .withSicCodes(newOrganisationSicCodeResource().withSicCode("code-1", "code-2").build(2))
                .withExecutiveOfficers(newOrganisationExecutiveOfficerResource().withName("director-1", "director-2").build(2))
                .build());

        AddressType addressType = newAddressType()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();

        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddress(newAddressResource()
                        .withAddressLine1("Line1")
                        .withAddressLine2("Line2")
                        .withAddressLine3("Line3")
                        .withCounty("County")
                        .withTown("Town")
                        .withCountry("Country")
                        .withPostcode("Postcode").build())
                .withAddressType(newAddressTypeResource()
                        .withId(OrganisationAddressType.REGISTERED.getId())
                        .withName(OrganisationAddressType.REGISTERED.name()).build())
                .build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(organisation));
        when(organisationAddressService.findByOrganisationIdAndAddressType(organisation.get(0).getId(), addressType))
                .thenReturn(serviceSuccess(Collections.singletonList(organisationAddressResource)));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());
        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));

        ReflectionTestUtils.setField(service, "newOrganisationSearchEnabled", true);
        ServiceResult<Void> result = service.syncCrmContact(userId, competitionId, applicationId);
        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContactWithOrganisationUpdates(user, organisation.get(0))));

        List<ILoggingEvent> eventList = memoryAppender.search("Payload", Level.INFO);
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }

    @Test
    public void syncExternalCrmContactWithExperienceTypeNotLoanShouldHaveAttributesStripped() {

        String expectedLogMessage = "Updating CRM contact test@innovate.com and organisation OrganisationResource 6 \n" +
                "Payload is:SilContact(ifsUuid=17a0e34c-719a-4db4-b011-ccd4c375ad79, experienceType=null, ifsAppID=null, email=test@innovate.com, lastName=Doe, firstName=Jon, title=null, " +
                "jobTitle=null, address=null, organisation=SilOrganisation(name=OrganisationResource 6, registrationNumber=null, registeredAddress=SilAddress(buildingName=Line1, street=Line2, Line3, " +
                "locality=County, town=Town, postcode=Postcode, country=Country), srcSysOrgId=6), sourceSystem=IFS, srcSysContactId=1) ";


        long userId = 1L;
        long applicationId = 3L;
        long competitionId = 4L;
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.GRANT);


        UserResource user = newUserResource()
                .withRoleGlobal(APPLICANT)
                .withId(1L)
                .withEmail("test@innovate.com")
                .withFirstName("Jon")
                .withLastName("Doe")
                .withUid("17a0e34c-719a-4db4-b011-ccd4c375ad79")
                .build();

        List<OrganisationResource> organisation = Arrays.asList(newOrganisationResource()
                .withDateOfIncorporation(LocalDate.now())
                .withSicCodes(newOrganisationSicCodeResource().withSicCode("code-1", "code-2").build(2))
                .withExecutiveOfficers(newOrganisationExecutiveOfficerResource().withName("director-1", "director-2").build(2))
                .build());

        AddressType addressType = newAddressType()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();

        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddress(newAddressResource()
                        .withAddressLine1("Line1")
                        .withAddressLine2("Line2")
                        .withAddressLine3("Line3")
                        .withCounty("County")
                        .withTown("Town")
                        .withCountry("Country")
                        .withPostcode("Postcode").build())
                .withAddressType(newAddressTypeResource()
                        .withId(OrganisationAddressType.REGISTERED.getId())
                        .withName(OrganisationAddressType.REGISTERED.name()).build())
                .build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(organisation));
        when(organisationAddressService.findByOrganisationIdAndAddressType(organisation.get(0).getId(), addressType))
                .thenReturn(serviceSuccess(Collections.singletonList(organisationAddressResource)));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());
        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));

        ReflectionTestUtils.setField(service, "newOrganisationSearchEnabled", true);
        ServiceResult<Void> result = service.syncCrmContact(userId, competitionId, applicationId);
        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContactWithOrganisationUpdates(user, organisation.get(0))));

        List<ILoggingEvent> eventList = memoryAppender.search("Payload", Level.INFO);
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }

    @Test
    public void syncMonitoringOfficerOnlyCrmContact() {
        long userId = 1L;
        UserResource user = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(Collections.emptyList()));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchMonitoringOfficerSilContact(user)));
    }

    @Test
    public void syncMonitoringOfficerAndExternalCrmContact() {
        long userId = 1L;
        UserResource user = newUserResource().withRolesGlobal(asList(APPLICANT, MONITORING_OFFICER)).build();
        List<OrganisationResource> organisations = newOrganisationResource().withCompaniesHouseNumber("Something", "Else").build(2);

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(organisations));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(0))));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(1))));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchMonitoringOfficerSilContact(user)));
    }


    private Predicate<SilContact> matchExternalSilContact(UserResource user, OrganisationResource organisation) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getRegistrationNumber(), equalTo(organisation.getCompaniesHouseNumber()));
            assertNull(silContact.getOrganisation().getRegisteredAddress());
            return true;
        };
    }

    private Predicate<SilContact> matchExternalSilContactWithOrganisationUpdates(UserResource user, OrganisationResource organisation) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getRegisteredAddress().getBuildingName(), equalTo("Line1"));
            assertThat(silContact.getOrganisation().getRegisteredAddress().getStreet(), equalTo("Line2, Line3"));
            assertThat(silContact.getOrganisation().getRegisteredAddress().getLocality(), equalTo("County"));
            assertThat(silContact.getOrganisation().getRegisteredAddress().getTown(), equalTo("Town"));
            assertThat(silContact.getOrganisation().getRegisteredAddress().getPostcode(), equalTo("Postcode"));
            assertThat(silContact.getOrganisation().getRegisteredAddress().getCountry(), equalTo("Country"));
            return true;
        };
    }

    private Predicate<SilContact> matchMonitoringOfficerSilContact(UserResource user) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getRegistrationNumber(), equalTo(""));
            return true;
        };
    }

    @Test
    public void syncCrmLoanApplicationSubmittedStateTest() {

        String expectedLogMessage = "Updating CRM application for appId:3 state:SUBMITTED, payload:SilLoanApplication(applicationID=3, " +
                "applicationSubmissionDate=2021-10-12T09:38:12.850Z, applicationName=Sample skips for plastic storage, applicationLocation=RG1 5LF," +
                " competitionCode=null, competitionName=null, projectDuration=11, projectTotalCost=10.0, projectOtherFunding=1.0, markedIneligible=null, eligibilityStatusChangeDate=null, eligibilityStatusChangeSource=null)";


        long applicationId = 3L;
        long competitionId = 4L;
        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.setId(applicationId);
        applicationResource.setApplicationState(ApplicationState.SUBMITTED);
        applicationResource.setSubmittedDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z"));
        applicationResource.setName("Sample skips for plastic storage");
        applicationResource.setDurationInMonths(11l);
        applicationResource.setCompetition(competitionId);
        applicationResource.setEvent("submitted");
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.LOAN);
        competitionResource.setCode("COMP-1");
        competitionResource.setName("Competition 1");

        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(applicationSummarisationService.getProjectTotalFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.TEN));
        when(applicationSummarisationService.getProjectOtherFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.ONE));
        when(applicationSummarisationService.getProjectLocation(applicationResource.getId())).thenReturn(serviceSuccess("RG1 5LF"));

        ServiceResult<Void> result = service.syncCrmApplicationState(applicationResource);

        List<ILoggingEvent> eventList = memoryAppender.search("payload:", Level.INFO);
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }


    @Test
    public void syncCrmLoanApplicationIneligibleStateTest() {
        String expectedLogMessage = "Updating CRM application for appId:3 state:INELIGIBLE, " +
                "payload:SilLoanApplication(applicationID=3, applicationSubmissionDate=null, applicationName=null, applicationLocation=null, competitionCode=null, competitionName=null, " +
                "projectDuration=null, projectTotalCost=null, projectOtherFunding=null, markedIneligible=true, eligibilityStatusChangeDate=2021-10-12T09:38:12.850Z[UTC], eligibilityStatusChangeSource=IFS)";


        long applicationId = 3L;
        long competitionId = 4L;
        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.setId(applicationId);
        applicationResource.setApplicationState(ApplicationState.INELIGIBLE);
        applicationResource.setSubmittedDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z"));
        applicationResource.setName("Sample skips for plastic storage");
        applicationResource.setDurationInMonths(11l);
        applicationResource.setCompetition(competitionId);
        applicationResource.setEvent(ApplicationEvent.MARK_INELIGIBLE.getType());
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.LOAN);


        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(applicationSummarisationService.getProjectTotalFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.TEN));
        when(applicationSummarisationService.getProjectOtherFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.ONE));
        when(applicationSummarisationService.getProjectLocation(applicationResource.getId())).thenReturn(serviceSuccess("RG1 5LF"));
        ReflectionTestUtils.setField(service, "eligibilityStatusChangeSource", "IFS");

        ServiceResult<Void> result = service.syncCrmApplicationState(applicationResource);

        List<ILoggingEvent> eventList = memoryAppender.search("payload:", Level.INFO);
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }

    @Test
    public void syncCrmLoanApplicationIneligibleInformedStateTest() {
        String expectedLogMessage = "Updating CRM application for appId:3 state:INELIGIBLE_INFORMED, " +
                "payload:SilLoanApplication(applicationID=3, applicationSubmissionDate=null, applicationName=null, applicationLocation=null, " +
                "competitionCode=null, competitionName=null, projectDuration=null, projectTotalCost=null, projectOtherFunding=null, markedIneligible=true, eligibilityStatusChangeDate=2021-10-12T09:38:12.850Z[UTC], eligibilityStatusChangeSource=IFS)";


        long applicationId = 3L;
        long competitionId = 4L;
        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.setId(applicationId);
        applicationResource.setApplicationState(ApplicationState.INELIGIBLE_INFORMED);
        applicationResource.setSubmittedDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z"));
        applicationResource.setName("Sample skips for plastic storage");
        applicationResource.setDurationInMonths(11l);
        applicationResource.setCompetition(competitionId);
        applicationResource.setEvent(ApplicationEvent.INFORM_INELIGIBLE.getType());
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.LOAN);


        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(applicationSummarisationService.getProjectTotalFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.TEN));
        when(applicationSummarisationService.getProjectOtherFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.ONE));
        when(applicationSummarisationService.getProjectLocation(applicationResource.getId())).thenReturn(serviceSuccess("RG1 5LF"));
        ReflectionTestUtils.setField(service, "eligibilityStatusChangeSource", "IFS");

        ServiceResult<Void> result = service.syncCrmApplicationState(applicationResource);

        List<ILoggingEvent> eventList = memoryAppender.search("payload:", Level.INFO);
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }

    @Test
    public void syncCrmLoanApplicationReinstatedStateTest() {

        String expectedLogMessage = "Updating CRM application for appId:3 state:SUBMITTED, " +
                "payload:SilLoanApplication(applicationID=3, applicationSubmissionDate=null, applicationName=null, applicationLocation=null, " +
                "competitionCode=null, competitionName=null, projectDuration=null, projectTotalCost=null, projectOtherFunding=null, " +
                "markedIneligible=false, eligibilityStatusChangeDate=2021-10-12T09:38:12.850Z[UTC], eligibilityStatusChangeSource=IFS)";


        long applicationId = 3L;
        long competitionId = 4L;
        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.setId(applicationId);
        applicationResource.setApplicationState(ApplicationState.SUBMITTED);
        applicationResource.setSubmittedDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z"));
        applicationResource.setName("Sample skips for plastic storage");
        applicationResource.setDurationInMonths(11l);
        applicationResource.setCompetition(competitionId);
        applicationResource.setEvent(ApplicationEvent.REINSTATE_INELIGIBLE.getType());
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.LOAN);


        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(applicationSummarisationService.getProjectTotalFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.TEN));
        when(applicationSummarisationService.getProjectOtherFunding(applicationResource.getId())).thenReturn(serviceSuccess(BigDecimal.ONE));
        when(applicationSummarisationService.getProjectLocation(applicationResource.getId())).thenReturn(serviceSuccess("RG1 5LF"));

        ServiceResult<Void> result = service.syncCrmApplicationState(applicationResource);


        List<ILoggingEvent> eventList = memoryAppender.search("payload:", Level.INFO);
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }

    @Test
    public void syncCrmCompetitionAssessmentTest() {
        Long competitionId = 15l;
        long applicationId1 = 366L;
        long applicationId2 = 367L;
        String expectedLogMessage = "Updating CRM application for compId:15,  " +
                "payload:SilLoanAssessment(competitionID=15, applications=[SilLoanAssessmentRow(applicationID=366, scoreAverage=55, scoreSpread=0, assessorNumber=2, assessorNotInScope=1, assessorRecommended=2, assessorNotRecommended=0), " +
                "SilLoanAssessmentRow(applicationID=367, scoreAverage=61, scoreSpread=2, assessorNumber=2, assessorNotInScope=0, assessorRecommended=1, assessorNotRecommended=1)])";


        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setFundingType(FundingType.LOAN);
        competitionResource.setId(competitionId);


        List<Application> applicationsCompsStream = Arrays.asList(newApplication()
                        .withId(applicationId1)
                        .withAssessments(Arrays.asList(AssessmentBuilder.newAssessment().withProcessState(SUBMITTED).build(),
                                AssessmentBuilder.newAssessment().withProcessState(SUBMITTED).build()))
                        .withCompetition(newCompetition().withId(competitionId).build()).build(),
                newApplication()
                        .withId(applicationId2)
                        .withAssessments(Arrays.asList(AssessmentBuilder.newAssessment().withProcessState(SUBMITTED).build(),
                                AssessmentBuilder.newAssessment().withProcessState(SUBMITTED).build()))
                        .withCompetition(newCompetition().withId(competitionId).build()).build());


        ApplicationAssessmentAggregateResource expected1 = new ApplicationAssessmentAggregateResource(true, 2, 1, Collections.emptyMap(), BigDecimal.valueOf(55));
        ApplicationAssessmentAggregateResource expected2 = new ApplicationAssessmentAggregateResource(true, 2, 2, Collections.emptyMap(), BigDecimal.valueOf(61));
        ApplicationAssessmentResource applicationAssessmentResource1 = newApplicationAssessmentResource()
                .withApplicationId(applicationId1)
                .withApplicationName("Loans Application1")
                .withAssessmentId(101L)
                .withLeadOrganisation("Lead Company")
                .withRecommended(true)
                .withOverallScore(55)
                .withState(SUBMITTED)
                .build();

        ApplicationAssessmentResource applicationAssessmentResource2 = newApplicationAssessmentResource()
                .withApplicationId(applicationId2)
                .withApplicationName("Loans Application1")
                .withAssessmentId(102L)
                .withLeadOrganisation("Lead Company")
                .withRecommended(true)
                .withOverallScore(55)
                .withState(SUBMITTED)
                .build();

        ApplicationAssessmentResource applicationAssessmentResource3 = newApplicationAssessmentResource()
                .withApplicationId(applicationId1)
                .withApplicationName("Loans Application2")
                .withAssessmentId(101L)
                .withLeadOrganisation("Lead Company2")
                .withRecommended(true)
                .withOverallScore(60)
                .withState(SUBMITTED)
                .build();

        ApplicationAssessmentResource applicationAssessmentResource4 = newApplicationAssessmentResource()
                .withApplicationId(applicationId2)
                .withApplicationName("Loans Application2")
                .withAssessmentId(102L)
                .withLeadOrganisation("Lead Company2")
                .withRecommended(false)
                .withOverallScore(62)
                .withState(SUBMITTED)
                .build();


        when(assessorFormInputResponseServiceMock.getApplicationAggregateScores(applicationId1)).thenReturn(serviceSuccess(expected1));
        when(assessorFormInputResponseServiceMock.getApplicationAggregateScores(applicationId2)).thenReturn(serviceSuccess(expected2));
        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(applicationService.getApplicationsByCompetitionIdAndState(any(), any())).thenReturn(ServiceResult.serviceSuccess(applicationsCompsStream));
        when(applicationAssessmentService.getApplicationAssessmentResource(applicationId1)).thenReturn(serviceSuccess(Arrays.asList(applicationAssessmentResource1, applicationAssessmentResource2)));
        when(applicationAssessmentService.getApplicationAssessmentResource(applicationId2)).thenReturn(serviceSuccess(Arrays.asList(applicationAssessmentResource3, applicationAssessmentResource4)));
        when(silCrmEndpoint.updateLoanAssessment(any(SilLoanAssessment.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmCompetitionAssessment(competitionId);
        List<ILoggingEvent> eventList = memoryAppender.search("payload:", Level.INFO);

        assertThat(result.isSuccess(), equalTo(true));
        assertEquals(expectedLogMessage, eventList.get(0).getMessage());
    }


}