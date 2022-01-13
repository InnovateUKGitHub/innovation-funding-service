package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeResourceBuilder.newIneligibleOutcomeResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ApplicationServiceImplTest extends BaseServiceUnitTest<ApplicationService> {

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private InviteRestService inviteRestService;

    private Long userId;

    private CompetitionResource openCompetition;
    private CompetitionResource inAssessmentCompetition;
    private CompetitionResource fundersPanelCompetition;
    private CompetitionResource closedCompetition;

    @Before
    public void setUp() {
        super.setup();

        userId = 1L;

        openCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
        inAssessmentCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();
        fundersPanelCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL).build();
        closedCompetition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).build();

        List<ApplicationResource> applications = newApplicationResource()
                .withId(1L, 2L, 3L, 4L, 5L, 6L)
                .withApplicationState(ApplicationState.CREATED,
                        ApplicationState.OPENED,
                        ApplicationState.OPENED,
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

        when(competitionRestService.getCompetitionById(openCompetition.getId())).thenReturn(restSuccess(openCompetition));
        when(competitionRestService.getCompetitionById(inAssessmentCompetition.getId())).thenReturn(restSuccess(inAssessmentCompetition));
        when(competitionRestService.getCompetitionById(fundersPanelCompetition.getId())).thenReturn(restSuccess(fundersPanelCompetition));
        when(competitionRestService.getCompetitionById(closedCompetition.getId())).thenReturn(restSuccess(closedCompetition));

        when(applicationRestService.getApplicationsByUserId(userId)).thenReturn(restSuccess(applications));

        when(inviteRestService.removeApplicationInvite(anyLong())).thenReturn(restSuccess());
    }

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Test
    public void getById() {
        Long applicationId = 3L;

        ApplicationResource application = new ApplicationResource();
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));

        ApplicationResource returnedApplication = service.getById(applicationId);

        assertEquals(application, returnedApplication);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetByIdNotFound() {
        Long applicationId = 5L;

        when(applicationRestService.getApplicationById(applicationId)).thenThrow(new ObjectNotFoundException("Application not found", asList(applicationId)));

        service.getById(applicationId);
    }

    @Test
    public void getByIdNullValue() {
        ApplicationResource returnedApplication = service.getById(null);

        assertEquals(null, returnedApplication);
    }

    @Test
    public void save() {
        ApplicationResource application = new ApplicationResource();

        ValidationMessages validationMessages = new ValidationMessages();

        when(applicationRestService.saveApplication(application)).thenReturn(restSuccess(validationMessages));

        ServiceResult<ValidationMessages> result = service.save(application);
        assertTrue(result.isSuccess());
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).saveApplication(application);
    }

    @Test
    public void removeCollaborator() {
        Long applicationInviteId = 80512L;
        ServiceResult<Void> result = service.removeCollaborator(applicationInviteId);
        assertTrue(result.isSuccess());
        Mockito.inOrder(inviteRestService).verify(inviteRestService, calls(1)).removeApplicationInvite(applicationInviteId);
    }

    @Test
    public void markAsIneligible() {
        long applicationId = 1L;
        IneligibleOutcomeResource reason = newIneligibleOutcomeResource()
                .withReason("reason")
                .build();

        when(applicationRestService.markAsIneligible(applicationId, reason)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.markAsIneligible(applicationId, reason);

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(applicationRestService);
        inOrder.verify(applicationRestService).markAsIneligible(applicationId, reason);
        inOrder.verifyNoMoreInteractions();
    }
}
