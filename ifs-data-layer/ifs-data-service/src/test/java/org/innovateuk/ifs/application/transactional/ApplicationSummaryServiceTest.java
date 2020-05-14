package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryPageMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.mapper.OrganisationApplicationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.PageableMatcher.srt;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.PreviousApplicationResourceBuilder.newPreviousApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus.*;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class ApplicationSummaryServiceTest extends BaseUnitTestMocksTest {

    private static final Long COMP_ID = Long.valueOf(123L);

    private static final Collection<ApplicationState> INELIGIBLE_STATES = asLinkedSet(
            ApplicationState.INELIGIBLE,
            ApplicationState.INELIGIBLE_INFORMED);

    @InjectMocks
    private ApplicationSummaryService applicationSummaryService = new ApplicationSummaryServiceImpl();

    @Mock
    private ApplicationSummaryMapper applicationSummaryMapper;

    @Mock
    private ApplicationSummaryPageMapper applicationSummaryPageMapper;

    @Mock
    private OrganisationApplicationAddressMapper organisationApplicationAddressMapper;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private ApplicationMapper applicationMapperMock;

    @Mock
    private ApplicationWorkflowHandler applicationWorkflowHandlerMock;

    @Mock
    private UserMapper userMapper;

    @Test
    public void findByCompetitionNoSortWillSortById() {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq("filter"), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, null, 6, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionNoFilterWillFilterByEmptyString() {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq(""), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);
        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, null, 6, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionSortById() {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq("filter"), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "id", 6, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionSortByName() {

        Page<Application> page = mock(Page.class);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(eq(COMP_ID), eq(INELIGIBLE_STATES), eq("filter"), argThat(new PageableMatcher(6, 20, srt("name", ASC), srt("id", ASC))))).thenReturn(page);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "name", 6, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionSortByLead() {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLead("b");
        ApplicationSummaryResource sum2 = sumLead("a");
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals(sum2, result.getSuccess().getContent().get(0));
        assertEquals(sum1, result.getSuccess().getContent().get(1));
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(2, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadSameLeadWillSortById() {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLead("a", 2L);
        ApplicationSummaryResource sum2 = sumLead("a", 1L);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals(sum2, result.getSuccess().getContent().get(0));
        assertEquals(sum1, result.getSuccess().getContent().get(1));
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(2, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadNotFirstPage() {

        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            Application app = mock(Application.class);
            applications.add(app);
            ApplicationSummaryResource sum = sumLead("a" + String.format("%02d", i));
            when(applicationSummaryMapper.mapToResource(app)).thenReturn(sum);
        }

        Collections.reverse(applications);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 1, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(1, result.getSuccess().getNumber());
        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals("a20", result.getSuccess().getContent().get(0).getLead());
        assertEquals("a21", result.getSuccess().getContent().get(1).getLead());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(22, result.getSuccess().getTotalElements());
        assertEquals(2, result.getSuccess().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadHandlesNullLeads() {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLead(null);
        ApplicationSummaryResource sum2 = sumLead(null);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals(sum1, result.getSuccess().getContent().get(0));
        assertEquals(sum2, result.getSuccess().getContent().get(1));
    }

    @Test
    public void findByCompetitionSortByLeadHandlesNullAndNotNullLead() {

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

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "lead", 0, 20, of("filter"));

        assertEquals(3, result.getSuccess().getContent().size());
        assertEquals(sum1, result.getSuccess().getContent().get(0));
        assertEquals(sum3, result.getSuccess().getContent().get(1));
        assertEquals(sum2, result.getSuccess().getContent().get(2));
    }

    @Test
    public void findByCompetitionSortByLeadApplicant() {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLeadApplicant("b");
        ApplicationSummaryResource sum2 = sumLeadApplicant("a");
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals(sum2, result.getSuccess().getContent().get(0));
        assertEquals(sum1, result.getSuccess().getContent().get(1));
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(2, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadSameLeadApplicantWillSortById() {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLeadApplicant("a", 2L);
        ApplicationSummaryResource sum2 = sumLeadApplicant("a", 1L);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals(sum2, result.getSuccess().getContent().get(0));
        assertEquals(sum1, result.getSuccess().getContent().get(1));
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(2, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadApplicantNotFirstPage() {

        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            Application app = mock(Application.class);
            applications.add(app);
            ApplicationSummaryResource sum = sumLeadApplicant("a" + String.format("%02d", i));
            when(applicationSummaryMapper.mapToResource(app)).thenReturn(sum);
        }

        Collections.reverse(applications);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 1, 20, of("filter"));

        assertTrue(result.isSuccess());
        assertEquals(1, result.getSuccess().getNumber());
        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals("a20", result.getSuccess().getContent().get(0).getLeadApplicant());
        assertEquals("a21", result.getSuccess().getContent().get(1).getLeadApplicant());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(22, result.getSuccess().getTotalElements());
        assertEquals(2, result.getSuccess().getTotalPages());
    }

    @Test
    public void findByCompetitionSortByLeadApplicantHandlesNullLeadApplicants() {

        Application app1 = mock(Application.class);
        Application app2 = mock(Application.class);
        List<Application> applications = asList(app1, app2);

        ApplicationSummaryResource sum1 = sumLeadApplicant(null);
        ApplicationSummaryResource sum2 = sumLeadApplicant(null);
        when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
        when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertEquals(2, result.getSuccess().getContent().size());
        assertEquals(sum1, result.getSuccess().getContent().get(0));
        assertEquals(sum2, result.getSuccess().getContent().get(1));
    }

    @Test
    public void findByCompetitionSortByLeadApplicantHandlesNullAndNotNullLead() {

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

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateNotIn(COMP_ID, INELIGIBLE_STATES, "filter")).thenReturn(applications);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, "leadApplicant", 0, 20, of("filter"));

        assertEquals(3, result.getSuccess().getContent().size());
        assertEquals(sum1, result.getSuccess().getContent().get(0));
        assertEquals(sum3, result.getSuccess().getContent().get(1));
        assertEquals(sum2, result.getSuccess().getContent().get(2));
    }

    @Test
    public void findByCompetitionSubmittedApplications() {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByApplicationStateAndFundingDecision(
                eq(COMP_ID),
                eq(asLinkedSet(APPROVED, REJECTED, SUBMITTED)),
                eq(""),
                eq(UNFUNDED),
                eq(null),
                argThat(new PageableMatcher(0, 20, srt("id", ASC)))))
                .thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService
                .getSubmittedApplicationSummariesByCompetitionId(
                        COMP_ID,
                        "id",
                        0,
                        20,
                        of(""),
                        of(UNFUNDED),
                        empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionIneligibleApplications() {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByApplicationStateAndFundingDecision(
                eq(COMP_ID),
                eq(asLinkedSet(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED)),
                eq(""),
                eq(null),
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
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionIneligibleApplications_informFiltered() {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByApplicationStateAndFundingDecision(
                eq(COMP_ID),
                eq(singleton(ApplicationState.INELIGIBLE_INFORMED)),
                eq(""),
                eq(null),
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
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findByCompetitionWithFundingDecisionApplications() {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByCompetitionIdAndFundingDecisionIsNotNull(eq(COMP_ID), eq("filter"), eq(false), eq(ON_HOLD), argThat(new PageableMatcher(0, 20, srt("id", ASC))))).thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(COMP_ID, "id", 0, 20, of("filter"), of(false), of(ON_HOLD));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void findWithFundingDecisionIsChangeableApplicationIdsByCompetitionId() {

        List<Application> applications = newApplication()
                .withManageFundingEmailDate(ZonedDateTime.now())
                .withFundingDecision(FUNDED)
                .build(2);

        when(applicationRepositoryMock.findByCompetitionIdAndFundingDecisionIsNotNull(eq(COMP_ID), eq("filter"), eq(false), eq(FUNDED))).thenReturn(applications);

        ServiceResult<List<Long>> result = applicationSummaryService.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(COMP_ID, of("filter"), of(false), of(FUNDED));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().size());
    }

    @Test
    public void findWithFundingDecisionIsNotChangeableApplicationIdsByCompetitionId() {

        List<Application> applications = newApplication()
                .withFundingDecision(ON_HOLD)
                .build(2);

        when(applicationRepositoryMock.findByCompetitionIdAndFundingDecisionIsNotNull(eq(COMP_ID), eq("filter"), eq(false), eq(FUNDED))).thenReturn(applications);

        ServiceResult<List<Long>> result = applicationSummaryService.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(COMP_ID, of("filter"), of(false), of(FUNDED));

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertEquals(applications.get(0).getId(), result.getSuccess().get(0));
        assertEquals(applications.get(1).getId(), result.getSuccess().get(1));
    }

    @Test
    public void getAllSubmittedApplicationIdsByCompetitionId() {
        List<Application> applications = newApplication()
                .withFundingDecision(UNFUNDED)
                .build(2);

        List<Long> ids = applications.stream().map(Application::getId).collect(Collectors.toList());

        when(applicationRepositoryMock.findApplicationIdsByApplicationStateAndFundingDecision(
                eq(COMP_ID), eq(SUBMITTED_STATES),  eq("filter"), eq(UNFUNDED), eq(null))).thenReturn(ids);

        ServiceResult<List<Long>> result = applicationSummaryService.getAllSubmittedApplicationIdsByCompetitionId(COMP_ID, of("filter"), of(UNFUNDED));
        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertEquals(applications.get(0).getId(), result.getSuccess().get(0));
        assertEquals(applications.get(1).getId(), result.getSuccess().get(1));
    }

    @Test
    public void getPreviousApplications() {
        long competitionId = 1L;
        List<PreviousApplicationResource> previousApplicationResources = newPreviousApplicationResource().build(1);

        when(applicationRepositoryMock.findPrevious(competitionId)).thenReturn(previousApplicationResources);

        ServiceResult<List<PreviousApplicationResource>> result = applicationSummaryService.getPreviousApplications(competitionId);

        assertEquals(previousApplicationResources, result.getSuccess());
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