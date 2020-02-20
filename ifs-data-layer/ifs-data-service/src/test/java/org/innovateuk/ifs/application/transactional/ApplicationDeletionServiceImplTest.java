package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationDeletionServiceImplTest extends BaseServiceUnitTest<ApplicationDeletionServiceImpl> {

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    @Mock
    private ProcessHistoryRepository processHistoryRepository;

    @Mock
    private DeletedApplicationRepository deletedApplicationRepository;

    @Mock
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Mock
    private UserRepository userRepository;

    @Override
    protected ApplicationDeletionServiceImpl supplyServiceUnderTest() {
        return new ApplicationDeletionServiceImpl();
    }

    @Test
    public void deleteApplication() {
        long applicationId = 1L;
        Application application = newApplication()
                .withApplicationState(ApplicationState.OPENED)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ServiceResult<Void> result = service.deleteApplication(applicationId);

        assertTrue(result.isSuccess());

        verify(applicationFinanceRepository).deleteByApplicationId(applicationId);
        verify(processRoleRepository).deleteByApplicationId(applicationId);
        verify(formInputResponseRepository).deleteByApplicationId(applicationId);
        verify(questionStatusRepository).deleteByApplicationId(applicationId);
        verify(applicationHiddenFromDashboardRepository).deleteByApplicationId(applicationId);
        verify(processHistoryRepository).deleteByProcessId(application.getApplicationProcess().getId());
        verify(applicationRepository).delete(application);

        verify(deletedApplicationRepository).save(any());
    }

    @Test
    public void hideApplicationFromDashboard() {
        long applicationId = 1L;
        long userId = 2L;
        Application application = newApplication()
                .build();
        User user = new User();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ServiceResult<Void> result = service.hideApplicationFromDashboard(ApplicationUserCompositeId.id(applicationId, userId));

        verify(applicationHiddenFromDashboardRepository).save(any());
    }

}