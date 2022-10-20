package org.innovateuk.ifs.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.crm.transactional.SilMessageRecordingService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.SilPayloadKeyType;
import org.innovateuk.ifs.sil.SilPayloadType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import static java.time.ZonedDateTime.now;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.documentation.OrganisationDocs.organisationResourceBuilder;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationDetailsControllerTest extends BaseControllerMockMVCTest<ApplicationDetailsController> {
    public static final String APPLICATION_PAYLOAD = "{\n" +
            "  \"appID\" : 2,\n" +
            "  \"appName\" : \"IM Application\",\n" +
            "  \"appStartDate\" : \"2022-08-14T23:00:00Z\",\n" +
            "  \"appSubmittedDate\" : \"2023-08-14T23:00:00Z\",\n" +
            "  \"compID\" : \"1\",\n" +
            "  \"fundingDecision\" : null,\n" +
            "  \"durationInMonths\" : null,\n" +
            "  \"completion\" : null,\n" +
            "  \"manageFundingEmailDate\" : \"2022-08-15T11:40:03.258837Z\",\n" +
            "  \"inAssessmentReviewPanel\" : false,\n" +
            "  \"companyAge\" : null,\n" +
            "  \"companyPrimaryFocus\" : null,\n" +
            "  \"organisations\" : [ {\n" +
            "    \"organisationID\" : 1,\n" +
            "    \"organisationName\" : \"Company name\",\n" +
            "    \"companiesHouseNo\" : \"0123456789\",\n" +
            "    \"internationalRegistrationNumber\" : null,\n" +
            "    \"organisationSize\" : \"Medium\",\n" +
            "    \"organisationType\" : \"Business\",\n" +
            "    \"internationalLocation\" : null,\n" +
            "    \"workPostcode\" : \"RH6 0NT\"\n" +
            "  } ]\n" +
            "}";
    @Mock
    private ApplicationService applicationService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private ApplicationFinanceService applicationFinanceService;

    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UsersRolesService usersRolesService;
    @Mock
    private SilMessageRecordingService silMessagingService;
    @Before
    public void  setup(){
        when(objectMapper.writer()).thenReturn(new ObjectMapper().writer());
        doNothing().when(silMessagingService).recordSilMessage(SilPayloadType.APPLICATION_UPDATE, SilPayloadKeyType.APPLICATION_ID,"1", APPLICATION_PAYLOAD, null);
    }
    @Test
    public void getApplicationDetailsByUnauthorzisedUser() throws Exception {
        long applicationId = 2L;
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(null);

        mockMvc.perform(get("/application-details/v1/{applicationId}", applicationId))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void getApplicationDetailsByUserNotInApplication() throws Exception {
        long applicationId = 2L;
        long competitionId = 1L;
        UserResource user = newUserResource()
                .withId(1L)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(GRANT)
                .build();

        Set<OrganisationResource> organisationResourceSet = organisationResourceBuilder.buildSet(1);
        Long organisationId = getOrganisationId(organisationResourceSet);

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withName("IM Application")
                .withStartDate(LocalDate.now())
                .withSubmittedDate(now())
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisationId)
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(serviceFailure(GENERAL_NOT_FOUND));
        when(applicationService.getApplicationById(applicationId)).thenReturn(serviceSuccess(application));
        when(applicationService.getCompetitionByApplicationId(application.getId())).thenReturn(serviceSuccess(competition));

        mockMvc.perform(get("/application-details/v1/{applicationId}", applicationId))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void getApplicationDetails() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;

        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(1L).build();

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(GRANT)
                .build();

        Set<OrganisationResource> organisationResourceSet = organisationResourceBuilder
                .withId(1L)
                .withOrganisationType(1L)
                .withOrganisationTypeName("Business")
                .buildSet(1);
        Long organisationId = getOrganisationId(organisationResourceSet);

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withName("IM Application")
                .withStartDate(LocalDate.now())
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisationId)
                .build();

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withOrganisation(organisationId)
                .withOrganisationSize(OrganisationSize.MEDIUM)
                .withWorkPostcode("RH6 0NT")
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(serviceSuccess(processRole));
        when(applicationService.getApplicationById(application.getId())).thenReturn(serviceSuccess(application));
        when(applicationService.getCompetitionByApplicationId(application.getId())).thenReturn(serviceSuccess(competition));
        when(applicationService.findLatestEmailFundingDateByCompetitionId(competition.getId())).thenReturn(serviceSuccess(ZonedDateTime.now()));
        when(organisationService.findByApplicationId(application.getId())).thenReturn(serviceSuccess(organisationResourceSet));
        when(applicationFinanceService.financeDetails(application.getId(), organisationId)).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/application-details/v1/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("appName", is("IM Application")))
                .andExpect(jsonPath("compID", is(Long.toString(competitionId))))
                .andExpect(jsonPath("organisations[0].organisationID", is(organisationId.intValue())))
                .andExpect(jsonPath("organisations[0].organisationSize", is("Medium")))
                .andExpect(jsonPath("organisations[0].organisationType", is("Business")))
                .andExpect(jsonPath("organisations[0].workPostcode", is("RH6 0NT")));
    }


    private Long getOrganisationId(Set<OrganisationResource> organisationResourceSet) {
        for (OrganisationResource org : organisationResourceSet) {
              return org.getId();
        }
        return 0L;
    }

    @Override
    protected ApplicationDetailsController supplyControllerUnderTest() {
        return new ApplicationDetailsController();
    }
}
