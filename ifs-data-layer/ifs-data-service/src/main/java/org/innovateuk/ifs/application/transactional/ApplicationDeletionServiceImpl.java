package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.innovateuk.ifs.application.domain.DeletedApplicationAudit;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional and secured service focused around the processing of Applications.
 */
@Service
public class ApplicationDeletionServiceImpl extends BaseTransactionalService implements ApplicationDeletionService {

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ProcessHistoryRepository processHistoryRepository;

    @Autowired
    private DeletedApplicationRepository deletedApplicationRepository;

    @Autowired
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Override
    @Transactional
    public ServiceResult<Void> deleteApplication(long applicationId) {
        return getApplication(applicationId).andOnSuccessReturnVoid((application) -> {
            applicationFinanceRepository.deleteByApplicationId(applicationId);
            processRoleRepository.deleteByApplicationId(applicationId);
            formInputResponseRepository.deleteByApplicationId(applicationId);
            questionStatusRepository.deleteByApplicationId(applicationId);
            applicationHiddenFromDashboardRepository.deleteByApplicationId(applicationId);
            processHistoryRepository.deleteByProcessId(application.getApplicationProcess().getId());
            applicationRepository.delete(application);
        }).andOnSuccessReturnVoid(() -> {
            deletedApplicationRepository.save(new DeletedApplicationAudit(applicationId));
        });
    }


    @Override
    @Transactional
    public ServiceResult<Void> hideApplicationFromDashboard(ApplicationUserCompositeId id) {
        return getApplication(id.getApplicationId()).andOnSuccessReturnVoid((application) ->
            getUser(id.getUserId()).andOnSuccessReturnVoid(user ->
                    applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user))));
    }
}
