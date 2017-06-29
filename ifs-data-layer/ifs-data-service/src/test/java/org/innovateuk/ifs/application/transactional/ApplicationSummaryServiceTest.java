package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryPageMapper;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.PageableMatcher.srt;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.domain.FundingDecisionStatus.ON_HOLD;
import static org.innovateuk.ifs.application.domain.FundingDecisionStatus.UNFUNDED;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class ApplicationSummaryServiceTest extends BaseUnitTestMocksTest {

    private static final Long COMP_ID = Long.valueOf(123L);

    private static final Collection<State> INELIGIBLE_STATES = simpleMapSet(asLinkedSet(
            ApplicationState.INELIGIBLE,
            ApplicationState.INELIGIBLE_INFORMED), ApplicationState::getBackingState);

    @InjectMocks
    private ApplicationSummaryService applicationSummaryService = new ApplicationSummaryServiceImpl();

    @Mock
    private ApplicationSummaryMapper applicationSummaryMapper;

    @Mock
    private ApplicationSummaryPageMapper applicationSummaryPageMapper;

    @Mock
    private OrganisationAddressMapper organisationAddressMapper;

    @Mock
    private UserMapper userMapper;

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionNoSortWillSortById() throws Exception {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq("filter"), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, null, 6, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionNoFilterWillFilterByEmptyString() throws Exception {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq(""), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);
        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, null, 6, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionSortById() throws Exception {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq("filter"), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "id", 6, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionSortByName() throws Exception {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq("filter"), argThat(new PageableMatcher(6, 20, srt("name", ASC), srt("id", ASC))))).thenReturn(page);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "name", 6, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccessObject());
    }

    @Test
    public void findByCompetitionSortByLead() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLead("b");
        ApplicationSummaryResource sum2 = sumLead("a");
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals(sum2, result.getSuccessObject().getContent().get(0));
        assertEquals(sum1, result.getSuccessObject().getContent().get(1));
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(2, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadSameLeadWillSortById() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLead("a", 2L);
        ApplicationSummaryResource sum2 = sumLead("a", 1L);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals(sum2, result.getSuccessObject().getContent().get(0));
        assertEquals(sum1, result.getSuccessObject().getContent().get(1));
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(2, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadNotFirstPage() throws Exception {

        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            Application app = mock(Application.class);
            applications.add(app);
            ApplicationSummaryResource sum = sumLead("a" + String.format("%02d", i));
            when(applicationSummaryMapper.mapToResource(app)).thenReturn(sum);
        }

        Collections.reverse(applications);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 1, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(1, result.getSuccessObject().getNumber());
        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals("a20", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals("a21", result.getSuccessObject().getContent().get(1).getLead());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(22, result.getSuccessObject().getTotalElements());
        assertEquals(2, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadHandlesNullLeads() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLead(null);
        ApplicationSummaryResource sum2 = sumLead(null);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals(sum1, result.getSuccessObject().getContent().get(0));
        assertEquals(sum2, result.getSuccessObject().getContent().get(1));
    }

    @Test
    public void findByCompetitionSortByLeadHandlesNullAndNotNullLead() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        Application app3 = mock(Application.class);
        List<Application> applications = asList(app1, app2, app3);

        ApplicationSummaryResource sum1 = sumLead(null);
        ApplicationSummaryResource sum2 = sumLead("a");
        ApplicationSummaryResource sum3 = sumLead(null);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
        when(applicationSummaryMapper.mapToResource(app3)).thenReturn(sum3);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertEquals(3, result.getSuccessObject().getContent().size());
        assertEquals(sum1, result.getSuccessObject().getContent().get(0));
        assertEquals(sum3, result.getSuccessObject().getContent().get(1));
        assertEquals(sum2, result.getSuccessObject().getContent().get(2));
    }

    @Test
    public void findByCompetitionSortByLeadApplicant() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLeadApplicant("b");
        ApplicationSummaryResource sum2 = sumLeadApplicant("a");
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals(sum2, result.getSuccessObject().getContent().get(0));
        assertEquals(sum1, result.getSuccessObject().getContent().get(1));
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(2, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadSameLeadApplicantWillSortById() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLeadApplicant("a", 2L);
        ApplicationSummaryResource sum2 = sumLeadApplicant("a", 1L);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals(sum2, result.getSuccessObject().getContent().get(0));
        assertEquals(sum1, result.getSuccessObject().getContent().get(1));
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(2, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadApplicantNotFirstPage() throws Exception {

        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            Application app = mock(Application.class);
            applications.add(app);
            ApplicationSummaryResource sum = sumLeadApplicant("a" + String.format("%02d", i));
            when(applicationSummaryMapper.mapToResource(app)).thenReturn(sum);
        }

        Collections.reverse(applications);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 1, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(1, result.getSuccessObject().getNumber());
        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals("a20", result.getSuccessObject().getContent().get(0).getLeadApplicant());
        assertEquals("a21", result.getSuccessObject().getContent().get(1).getLeadApplicant());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(22, result.getSuccessObject().getTotalElements());
        assertEquals(2, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadApplicantHandlesNullLeadApplicants() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLeadApplicant(null);
        ApplicationSummaryResource sum2 = sumLeadApplicant(null);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertEquals(2, result.getSuccessObject().getContent().size());
        assertEquals(sum1, result.getSuccessObject().getContent().get(0));
        assertEquals(sum2, result.getSuccessObject().getContent().get(1));
    }

    @Test
    public void findByCompetitionSortByLeadApplicantHandlesNullAndNotNullLead() throws Exception {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        Application app3 = mock(Application.class);
        List<Application> applications = asList(app1, app2, app3);

        ApplicationSummaryResource sum1 = sumLeadApplicant(null);
        ApplicationSummaryResource sum2 = sumLeadApplicant("a");
        ApplicationSummaryResource sum3 = sumLeadApplicant(null);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
        when(applicationSummaryMapper.mapToResource(app3)).thenReturn(sum3);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertEquals(3, result.getSuccessObject().getContent().size());
        assertEquals(sum1, result.getSuccessObject().getContent().get(0));
        assertEquals(sum3, result.getSuccessObject().getContent().get(1));
        assertEquals(sum2, result.getSuccessObject().getContent().get(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionSubmittedApplications() throws Exception {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                eq(COMP_ID),
                eq(simpleMapSet(asLinkedSet(APPROVED, REJECTED, SUBMITTED), ApplicationState::getBackingState)),
                eq(""),
                eq(UNFUNDED),
                argThat(new PageableMatcher(0, 20, srt("id", ASC)))))
                .thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService
                .getSubmittedApplicationSummariesByCompetitionId(
                        COMP_ID,
                        "id",
                        0,
                        20,
                        of(""),
                        of(UNFUNDED));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(resource, result.getSuccessObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionIneligibleApplications() throws Exception {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                eq(COMP_ID),
                eq(simpleMapSet(asLinkedSet(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED), ApplicationState::getBackingState)),
                eq(""),
                eq(null),
                argThat(new PageableMatcher(0, 20, srt("id", ASC)))))
                .thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService
                .getIneligibleApplicationSummariesByCompetitionId(
                        COMP_ID,
                        "id",
                        0,
                        20,
                        of(""),
                        empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(resource, result.getSuccessObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionIneligibleApplications_informFiltered() throws Exception {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                eq(COMP_ID),
                eq(singleton(ApplicationState.INELIGIBLE_INFORMED.getBackingState())),
                eq(""),
                eq(null),
                argThat(new PageableMatcher(0, 20, srt("id", ASC)))))
                .thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService
                .getIneligibleApplicationSummariesByCompetitionId(
                        COMP_ID,
                        "id",
                        0,
                        20,
                        of(""),
                        of(true));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(resource, result.getSuccessObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionWithFundingDecisionApplications() throws Exception {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByCompetitionIdAndFundingDecisionIsNotNull(eq(COMP_ID), eq("filter"), eq(false), eq(ON_HOLD), argThat(new PageableMatcher(0, 20, srt("id", ASC))))).thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(COMP_ID, "id", 0, 20, of("filter"), of(false), of(ON_HOLD));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(resource, result.getSuccessObject());
    }

    @Test
    public void getApplicationTeamSuccess() {
        Role leadRole = newRole().withType(UserRoleType.LEADAPPLICANT).build();
        User leadOrgLeadUser = newUser().withFirstName("Lee").withLastName("Der").withRoles(singletonList(leadRole).stream().collect(Collectors.toSet())).build();
        User leadOrgNonLeadUser1 = newUser().withFirstName("A").withLastName("Bee").build();
        User leadOrgNonLeadUser2 = newUser().withFirstName("Cee").withLastName("Dee").build();
        User partnerOrgLeadUser1 = newUser().withFirstName("Zee").withLastName("Der").withRoles(singletonList(leadRole).stream().collect(Collectors.toSet())).build();
        User partnerOrgLeadUser2 = newUser().withFirstName("Ay").withLastName("Der").withRoles(singletonList(leadRole).stream().collect(Collectors.toSet())).build();


        ProcessRole lead = newProcessRole().withRole(UserRoleType.LEADAPPLICANT).withOrganisationId(234L).withUser(leadOrgLeadUser).build();
        ProcessRole collaborator1 = newProcessRole().withRole(UserRoleType.COLLABORATOR).withOrganisationId(345L).withUser(partnerOrgLeadUser1).build();
        ProcessRole collaborator2 = newProcessRole().withRole(UserRoleType.COLLABORATOR).withOrganisationId(456L).withUser(partnerOrgLeadUser2).build();
        Application app = newApplication().withProcessRoles(lead, collaborator1, collaborator2).build();

        AddressType registeredAddressType = newAddressType().withName("REGISTERED").build();
        AddressType operatingAddressType = newAddressType().withName("OPERATING").build();
        Address address1 = newAddress().withAddressLine1("1E").withAddressLine2("2.16").withAddressLine3("Polaris House").withTown("Swindon").withCounty("Wilts").withPostcode("SN1 1AA").build();
        Address address2 = newAddress().withAddressLine1("2E").withAddressLine2("2.17").withAddressLine3("North Star House").withTown("Swindon").withCounty("Wiltshire").withPostcode("SN2 2AA").build();
        OrganisationAddress leadOrgRegisteredAddress = newOrganisationAddress().withAddressType(registeredAddressType).withAddress(address1).build();
        OrganisationAddress leadOrgOperatingAddress = newOrganisationAddress().withAddressType(operatingAddressType).withAddress(address2).build();

        Organisation leadOrg = newOrganisation().withName("Lead").withUser(Arrays.asList(leadOrgLeadUser,leadOrgNonLeadUser1,leadOrgNonLeadUser2)).withAddress(Arrays.asList(leadOrgRegisteredAddress, leadOrgOperatingAddress)).build();
        Organisation partnerOrgA = newOrganisation().withName("A").withUser(singletonList(partnerOrgLeadUser1)).withAddress(singletonList(leadOrgRegisteredAddress)).build();
        Organisation partnerOrgB = newOrganisation().withName("B").withUser(singletonList(partnerOrgLeadUser2)).withAddress(singletonList(leadOrgOperatingAddress)).build();
        when(applicationRepositoryMock.findOne(123L)).thenReturn(app);
        when(organisationRepositoryMock.findOne(234L)).thenReturn(leadOrg);
        when(organisationRepositoryMock.findOne(345L)).thenReturn(partnerOrgB);
        when(organisationRepositoryMock.findOne(456L)).thenReturn(partnerOrgA);

        AddressTypeResource registeredAddressTypeResource = newAddressTypeResource().withName("REGISTERED").build();
        AddressTypeResource operatingAddressTypeResource = newAddressTypeResource().withName("OPERATING").build();
        AddressResource addressResource1 = newAddressResource().withAddressLine1("1E").withAddressLine2("2.16").withAddressLine3("Polaris House").withTown("Swindon").withCounty("Wilts").withPostcode("SN1 1AA").build();
        AddressResource addressResource2 = newAddressResource().withAddressLine1("2E").withAddressLine2("2.17").withAddressLine3("North Star House").withTown("Swindon").withCounty("Wiltshire").withPostcode("SN2 2AA").build();
        OrganisationAddressResource leadOrgRegisteredAddressResource = newOrganisationAddressResource().withAddressType(registeredAddressTypeResource).withAddress(addressResource1).build();
        OrganisationAddressResource leadOrgOperatingAddressResource = newOrganisationAddressResource().withAddressType(operatingAddressTypeResource).withAddress(addressResource2).build();
        when(organisationAddressMapper.mapToResource(leadOrgRegisteredAddress)).thenReturn(leadOrgRegisteredAddressResource);
        when(organisationAddressMapper.mapToResource(leadOrgOperatingAddress)).thenReturn(leadOrgOperatingAddressResource);

        UserResource leadOrgLeadUserResource = newUserResource().withFirstName("Lee").withLastName("Der").build();
        UserResource leadOrgNonLeadUser1Resource = newUserResource().withFirstName("A").withLastName("Bee").build();
        UserResource leadOrgNonLeadUser2Resource = newUserResource().withFirstName("Cee").withLastName("Dee").build();
        UserResource partnerOrgLeadUser1Resource = newUserResource().withFirstName("Zee").withLastName("Der").build();
        UserResource partnerOrgLeadUser2Resource = newUserResource().withFirstName("Ay").withLastName("Der").build();
        when(userMapper.mapToResource(leadOrgLeadUser)).thenReturn(leadOrgLeadUserResource);
        when(userMapper.mapToResource(leadOrgNonLeadUser1)).thenReturn(leadOrgNonLeadUser1Resource);
        when(userMapper.mapToResource(leadOrgNonLeadUser2)).thenReturn(leadOrgNonLeadUser2Resource);
        when(userMapper.mapToResource(partnerOrgLeadUser1)).thenReturn(partnerOrgLeadUser1Resource);
        when(userMapper.mapToResource(partnerOrgLeadUser2)).thenReturn(partnerOrgLeadUser2Resource);

        ServiceResult<ApplicationTeamResource> result = applicationSummaryService.getApplicationTeamByApplicationId(123L);
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject().getLeadOrganisation().getOrganisationName().equals("Lead"));
        assertTrue(result.getSuccessObject().getLeadOrganisation().getRegisteredAddress().getAddress().getAddressLine1().equals("1E"));
        assertTrue(result.getSuccessObject().getLeadOrganisation().getOperatingAddress().getAddress().getAddressLine1().equals("2E"));
        assertTrue(result.getSuccessObject().getLeadOrganisation().getUsers().get(0).getName().equals("Lee Der"));

        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(0).getOrganisationName().equals("A"));
        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(0).getRegisteredAddress().getAddress().getAddressLine1().equals("1E"));
        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(0).getOperatingAddress() == null);
        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(0).getUsers().get(0).getName().equals("Ay Der"));

        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(1).getOrganisationName().equals("B"));
        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(1).getRegisteredAddress() == null);
        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(1).getOperatingAddress().getAddress().getAddressLine1().equals("2E"));
        assertTrue(result.getSuccessObject().getPartnerOrganisations().get(1).getUsers().get(0).getName().equals("Zee Der"));
    }

    @Test
    public void getApplicationTeamFailsNoApplication() {

        when(applicationRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<ApplicationTeamResource> result = applicationSummaryService.getApplicationTeamByApplicationId(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().getErrors().get(0).getErrorKey().equals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey()));
    }

    private ApplicationSummaryResource sumLead(String lead) {
        ApplicationSummaryResource res = new ApplicationSummaryResource();
        res.setLead(lead);
        return res;
    }

    private ApplicationSummaryResource sumLead(String lead, Long id) {
        ApplicationSummaryResource res = sumLead(lead);
        res.setId(id);
        return res;
    }

    private ApplicationSummaryResource sumLeadApplicant(String leadApplicant) {
        ApplicationSummaryResource res = new ApplicationSummaryResource();
        res.setLeadApplicant(leadApplicant);
        return res;
    }

    private ApplicationSummaryResource sumLeadApplicant(String leadApplicant, Long id) {
        ApplicationSummaryResource res = sumLeadApplicant(leadApplicant);
        res.setId(id);
        return res;
    }
}
