package org.innovateuk.ifs.project.core.transactional;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectToBeCreatedServiceImplTest extends BaseServiceUnitTest<ProjectToBeCreatedService> {

    @Override
    protected ProjectToBeCreatedService supplyServiceUnderTest() {
        return new ProjectToBeCreatedServiceImpl();
    }

    @Mock
    private ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationFundingService applicationFundingService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private KtpProjectNotificationService ktpProjectNotificationService;

    @Test
    public void findProjectToCreate() {
        int index = 0;
        Pageable pageRequest = PageRequest.of(index, 1, Direction.ASC, "application.id");
        Application application = newApplication().build();
        ProjectToBeCreated projectToBeCreated = new ProjectToBeCreated(application, "");
        Page<ProjectToBeCreated> page = new PageImpl<>(newArrayList(projectToBeCreated), pageRequest, 1);

        when(projectToBeCreatedRepository.findByPendingIsTrue(pageRequest)).thenReturn(page);

        Optional<Long> findProjectToCreate = service.findProjectToCreate(index);

        assertThat(findProjectToCreate.get(), equalTo(application.getId()));
    }

    @Test
    public void createProject() {
        String emailMessage = "message";
        Application application = newApplication()
                .withCompetition(newCompetition().withFundingType(FundingType.GRANT).build())
                .build();
        ProjectToBeCreated projectToBeCreated = new ProjectToBeCreated(application, emailMessage);
        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource(emailMessage,
                ImmutableMap.<Long, FundingDecision> builder()
                        .put(application.getId(), FUNDED)
                        .build());

        when(projectToBeCreatedRepository.findByApplicationId(application.getId())).thenReturn(of(projectToBeCreated));
        when(applicationFundingService.notifyApplicantsOfFundingDecisions(fundingNotificationResource)).thenReturn(serviceSuccess());
        when(projectService.createProjectFromApplication(application.getId())).thenReturn(serviceSuccess(null));

        ServiceResult<ScheduleResponse> result = service.createProject(application.getId());

        assertTrue(result.isSuccess());
        assertEquals("Project created: " + application.getId(), result.getSuccess().getResponse());
        assertFalse(projectToBeCreated.isPending());

        verify(applicationFundingService).notifyApplicantsOfFundingDecisions(fundingNotificationResource);
        verify(projectService).createProjectFromApplication(application.getId());
        verifyZeroInteractions(ktpProjectNotificationService);
    }

    @Test
    public void createProject_ktp() {
        long applicationId = 1L;
        String emailMessage = "message";
        Application application = newApplication()
                .withCompetition(newCompetition().withFundingType(FundingType.KTP).build())
                .build();
        ProjectToBeCreated projectToBeCreated = new ProjectToBeCreated(application, emailMessage);

        when(projectToBeCreatedRepository.findByApplicationId(applicationId)).thenReturn(of(projectToBeCreated));
        when(projectService.createProjectFromApplication(application.getId())).thenReturn(serviceSuccess(null));
        when(ktpProjectNotificationService.sendProjectSetupNotification(application.getId())).thenReturn(serviceSuccess());

        ServiceResult<ScheduleResponse> result = service.createProject(applicationId);

        assertTrue(result.isSuccess());
        assertEquals("Project created: " + applicationId, result.getSuccess().getResponse());
        assertFalse(projectToBeCreated.isPending());

        verifyZeroInteractions(applicationFundingService);
        verify(projectService).createProjectFromApplication(application.getId());
        verify(ktpProjectNotificationService, times(1)).sendProjectSetupNotification(application.getId());
    }

    @Test
    public void createProject_ktp_failed_notification() {
        long applicationId = 1L;
        String emailMessage = "message";
        Application application = newApplication()
                .withCompetition(newCompetition().withFundingType(FundingType.KTP).build())
                .build();
        ProjectToBeCreated projectToBeCreated = new ProjectToBeCreated(application, emailMessage);

        when(projectToBeCreatedRepository.findByApplicationId(applicationId)).thenReturn(of(projectToBeCreated));
        when(projectService.createProjectFromApplication(application.getId())).thenReturn(serviceSuccess(null));
        when(ktpProjectNotificationService.sendProjectSetupNotification(application.getId()))
                .thenReturn(serviceFailure(new Error(CommonFailureKeys.GENERAL_NOT_FOUND)));

        ServiceResult<ScheduleResponse> result = service.createProject(applicationId);

        assertFalse(result.isSuccess());

        verifyZeroInteractions(applicationFundingService);
        verify(projectService).createProjectFromApplication(application.getId());
        verify(ktpProjectNotificationService, times(1)).sendProjectSetupNotification(application.getId());
    }

    @Test
    public void markApplicationReadyToBeCreated() {
        String emailMessage = "message";
        Application application = newApplication()
                .build();
        ProjectToBeCreated projectToBeCreated = new ProjectToBeCreated(application, emailMessage);

        when(projectToBeCreatedRepository.findByApplicationId(application.getId())).thenReturn(of(projectToBeCreated));

        ServiceResult<Void> result = service.markApplicationReadyToBeCreated(application.getId(), "emailBody");

        assertTrue(result.isSuccess());
        assertTrue(projectToBeCreated.isPending());
    }

    @Test
    public void markApplicationReadyToBeCreated_noExisting() {
        String emailMessage = "message";
        Application application = newApplication()
                .build();

        when(projectToBeCreatedRepository.findByApplicationId(application.getId())).thenReturn(empty());
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));

        ServiceResult<Void> result = service.markApplicationReadyToBeCreated(application.getId(), emailMessage);

        assertTrue(result.isSuccess());

        verify(projectToBeCreatedRepository).save(refEq(new ProjectToBeCreated(application, emailMessage)));
    }
}