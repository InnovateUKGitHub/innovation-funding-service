package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        openCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
        inAssessmentCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();
        fundersPanelCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL).build();
        closedCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).build();

        List<ApplicationResource> applications = newApplicationResource()
                .withId(1L, 2L, 3L, 4L, 5L, 6L)
                .withApplicationState(ApplicationState.CREATED,
                        ApplicationState.OPEN,
                        ApplicationState.OPEN,
                        ApplicationState.SUBMITTED,
                        ApplicationState.SUBMITTED,
                        ApplicationState.SUBMITTED)
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

        when(applicationRestService.getApplicationById(applicationId)).thenThrow(new ObjectNotFoundException("Application not found", asList(applicationId)));

        service.getById(applicationId);
    }

    @Test
    public void testGetByIdNullValue() throws Exception {
        ApplicationResource returnedApplication = service.getById(null);

        assertEquals(null, returnedApplication);
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Long applicationId = 2L;
        ApplicationState status = ApplicationState.APPROVED;
        when(applicationRestService.updateApplicationState(applicationId, status)).thenReturn(restSuccess());
        service.updateState(applicationId, status);
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).updateApplicationState(applicationId, status);
    }

    @Test
    public void testSave() throws Exception {
        ApplicationResource application = new ApplicationResource();

        when(applicationRestService.saveApplication(application)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.save(application);
        assertTrue(result.isSuccess());
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).saveApplication(application);
    }

    @Test
    public void testRemoveCollaborator() throws Exception {
        Long applicationInviteId = 80512L;
        ServiceResult<Void> result = service.removeCollaborator(applicationInviteId);
        assertTrue(result.isSuccess());
        Mockito.inOrder(inviteRestService).verify(inviteRestService, calls(1)).removeApplicationInvite(applicationInviteId);
    }
}
