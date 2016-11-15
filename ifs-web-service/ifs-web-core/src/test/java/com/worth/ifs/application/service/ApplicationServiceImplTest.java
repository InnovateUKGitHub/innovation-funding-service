package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.invite.service.InviteRestService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;

public class ApplicationServiceImplTest extends BaseServiceUnitTest<ApplicationService> {

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CompetitionsRestService competitionsRestService;

    @Mock
    private InviteRestService inviteRestService;

    private Long userId;
    
    private CompetitionResource openCompetition;
    private CompetitionResource inAssessmentCompetition;
    private CompetitionResource fundersPanelCompetition;
    private CompetitionResource closedCompetition;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        userId = 1L;

        openCompetition = newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.OPEN).build();
        inAssessmentCompetition = newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.IN_ASSESSMENT).build();
        fundersPanelCompetition = newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.FUNDERS_PANEL).build();
        closedCompetition = newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.ASSESSOR_FEEDBACK).build();

        List<ApplicationResource> applications = newApplicationResource()
        		.withId(1L, 2L, 3L, 4L, 5L, 6L)
        		.withApplicationStatus(ApplicationStatusConstants.CREATED.getId(),
                        ApplicationStatusConstants.OPEN.getId(),
                        ApplicationStatusConstants.OPEN.getId(),
                        ApplicationStatusConstants.SUBMITTED.getId(),
                        ApplicationStatusConstants.SUBMITTED.getId(),
                        ApplicationStatusConstants.SUBMITTED.getId())
                .withCompletion(ZERO, new BigDecimal("20.50"), ZERO, ZERO, ZERO, ZERO)
                .withCompetition(openCompetition.getId(),
                        openCompetition.getId(),
                        inAssessmentCompetition.getId(),
                        inAssessmentCompetition.getId(),
                        fundersPanelCompetition.getId(),
		        				closedCompetition.getId())
        		.build(6);

        when(competitionsRestService.getCompetitionById(openCompetition.getId())).thenReturn(restSuccess(openCompetition));
        when(competitionsRestService.getCompetitionById(inAssessmentCompetition.getId())).thenReturn(restSuccess(inAssessmentCompetition));
        when(competitionsRestService.getCompetitionById(fundersPanelCompetition.getId())).thenReturn(restSuccess(fundersPanelCompetition));
        when(competitionsRestService.getCompetitionById(closedCompetition.getId())).thenReturn(restSuccess(closedCompetition));

    	when(applicationRestService.getApplicationsByUserId(userId)).thenReturn(restSuccess(applications));

        when(inviteRestService.removeApplicationInvite(anyLong())).thenReturn(restSuccess());
    }

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Test
     public void testGetById() throws Exception {
    	Long applicationId = 3L;

    	ApplicationResource application = new ApplicationResource();
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));

        ApplicationResource returnedApplication = service.getById(applicationId);
        
        assertEquals(application, returnedApplication);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testGetByIdNotFound() throws Exception {
        Long applicationId = 5L;
        
        when(applicationRestService.getApplicationById(applicationId)).thenThrow(new ObjectNotFoundException("com.worth.ifs.Application not found", asList(applicationId)));
        
        service.getById(applicationId);
    }

    @Test
    public void testGetByIdNullValue() throws Exception {
        ApplicationResource returnedApplication = service.getById(null);
        
        assertEquals(null, returnedApplication);
    }

    @Test
    public void testGetInProgressReliesOnApplicationStatusAndCompetitionStatus() throws Exception {
        List<ApplicationResource> returnedApplications = service.getInProgress(userId);
        assertEquals(4, returnedApplications.size());
        assertEquals(ApplicationStatusConstants.CREATED.getId(), returnedApplications.get(0).getApplicationStatus());
        assertEquals(openCompetition.getId(), returnedApplications.get(0).getCompetition());

        assertEquals(ApplicationStatusConstants.OPEN.getId(), returnedApplications.get(1).getApplicationStatus());
        assertEquals(openCompetition.getId(), returnedApplications.get(1).getCompetition());
        
        assertEquals(ApplicationStatusConstants.SUBMITTED.getId(), returnedApplications.get(2).getApplicationStatus());
        assertEquals(inAssessmentCompetition.getId(), returnedApplications.get(2).getCompetition());
        
        assertEquals(ApplicationStatusConstants.SUBMITTED.getId(), returnedApplications.get(3).getApplicationStatus());
        assertEquals(fundersPanelCompetition.getId(), returnedApplications.get(3).getCompetition());
    }

    @Test
    public void testGetFinishedReliesOnApplicationStatusAndCompetitionStatus() throws Exception {
        List<ApplicationResource> returnedApplications = service.getFinished(userId);
        assertEquals(2, returnedApplications.size());
        assertEquals(ApplicationStatusConstants.OPEN.getId(), returnedApplications.get(0).getApplicationStatus());
        assertEquals(inAssessmentCompetition.getId(), returnedApplications.get(0).getCompetition());

        assertEquals(ApplicationStatusConstants.SUBMITTED.getId(), returnedApplications.get(1).getApplicationStatus());
        assertEquals(closedCompetition.getId(), returnedApplications.get(1).getCompetition());
    }
    
    @Test
     public void testGetProgress() throws Exception {
    	Long applicationId = 2L;
        
        Map<Long, Integer> progress = service.getProgress(userId);
        
        assertEquals(20, progress.get(applicationId), 0d);
    }

    @Test
    public void testGetProgressZero() throws Exception {
    	Long applicationId = 7L;
        
        Map<Long, Integer> progress = service.getProgress(userId);
        
        assertNull(progress.get(applicationId));
    }

    @Test
    public void testUpdateStatus() throws Exception {
    	Long applicationId = 2L;
        Long statusId = 1L;
        service.updateStatus(applicationId, statusId);
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).updateApplicationStatus(applicationId, statusId);
    }

    @Test
    public void testGetCompleteQuestionsPercentage() throws Exception {
    	Long applicationId = 3L;
        when(applicationRestService.getCompleteQuestionsPercentage(applicationId)).thenReturn(settable(restSuccess(20.5d)));

        // somehow the progress is rounded, because we use a long as the return type.
        Assert.assertEquals(20, service.getCompleteQuestionsPercentage(applicationId).intValue());
    }

    @Test
    public void testSave() throws Exception {
    	ApplicationResource application = new ApplicationResource();
        service.save(application);
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).saveApplication(application);
    }

    @Test
    public void testRemoveCollaborator() throws Exception {
        Long applicationInviteId = 80512L;
        service.removeCollaborator(applicationInviteId);
        Mockito.inOrder(inviteRestService).verify(inviteRestService, calls(1)).removeApplicationInvite(applicationInviteId);
    }

}