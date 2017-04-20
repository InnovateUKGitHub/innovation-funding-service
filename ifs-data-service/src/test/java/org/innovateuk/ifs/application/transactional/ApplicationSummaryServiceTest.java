package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.PageableMatcher;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryPageMapper;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.PageableMatcher.srt;
import static org.innovateuk.ifs.application.domain.FundingDecisionStatus.ON_HOLD;
import static org.innovateuk.ifs.application.domain.FundingDecisionStatus.UNFUNDED;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class ApplicationSummaryServiceTest extends BaseUnitTestMocksTest {

    private static final Long COMP_ID = Long.valueOf(123L);

    private static final Collection<State> INELIGIBLE_STATES = simpleMap(asList(
            ApplicationState.INELIGIBLE,
            ApplicationState.INELIGIBLE_INFORMED), ApplicationState::getBackingState);

    @InjectMocks
    private ApplicationSummaryService applicationSummaryService = new ApplicationSummaryServiceImpl();

    @Mock
    private ApplicationSummaryMapper applicationSummaryMapper;

    @Mock
    private ApplicationSummaryPageMapper applicationSummaryPageMapper;

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
                eq(simpleMap(asList(APPROVED, REJECTED, SUBMITTED), ApplicationState::getBackingState)),
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
                eq(simpleMap(asList(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED), ApplicationState::getBackingState)),
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
                        of(""));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(resource, result.getSuccessObject());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void findByCompetitionFeedbackRequiredApplications() throws Exception {

        Page<Application> page = mock(Page.class);

        ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
        when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateInAndAssessorFeedbackFileEntryIsNull(eq(COMP_ID), eq(asList(APPROVED.getBackingState(), REJECTED.getBackingState())), argThat(new PageableMatcher(0, 20, srt("id", ASC))))).thenReturn(page);

        ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getFeedbackRequiredApplicationSummariesByCompetitionId(COMP_ID, "id", 0, 20);

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

    private ApplicationSummaryResource sumPercentage(Integer percentage) {
        ApplicationSummaryResource res = new ApplicationSummaryResource();
        res.setCompletedPercentage(percentage);
        return res;
    }

    private ApplicationSummaryResource sumPercentage(Integer percentage, Long id) {
        ApplicationSummaryResource res = sumPercentage(percentage);
        res.setId(id);
        return res;
    }
}
