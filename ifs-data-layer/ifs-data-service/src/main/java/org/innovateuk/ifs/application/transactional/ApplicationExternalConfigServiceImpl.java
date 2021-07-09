package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.ApplicationExternalConfig;
import org.innovateuk.ifs.application.mapper.ApplicationExternalConfigMapper;
import org.innovateuk.ifs.application.repository.ApplicationExternalConfigRepository;
import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationExternalConfigServiceImpl extends RootTransactionalService implements ApplicationExternalConfigService {

    @Autowired
    private ApplicationExternalConfigRepository applicationExternalConfigRepository;

    @Autowired
    private ApplicationExternalConfigMapper mapper;

    @Override
    public ServiceResult<ApplicationExternalConfigResource> findOneByApplicationId(long applicationId) {
        return find(applicationExternalConfigRepository.findOneByApplicationId(applicationId), notFoundError(ApplicationExternalConfig.class, applicationId))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long applicationId, ApplicationExternalConfigResource applicationExternalConfigResource) {
        return find(applicationExternalConfigRepository.findOneByApplicationId(applicationId), notFoundError(ApplicationExternalConfig.class, applicationId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setExternalApplicationId(applicationExternalConfigResource.getExternalApplicationId());
                    config.setExternalApplicantName(applicationExternalConfigResource.getExternalApplicantName());
                });
    }
}
