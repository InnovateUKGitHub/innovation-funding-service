package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional
public class AssessmentPanelServiceImpl implements AssessmentPanelService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public ServiceResult<Void> assignApplicationToPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> application.setInAssessmentPanel(true));
    }

    @Override
    public ServiceResult<Void> unAssignApplicationFromPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> application.setInAssessmentPanel(false));
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }
}
