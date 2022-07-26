package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceResponse;
import org.innovateuk.ifs.application.mapper.ApplicationEoiEvidenceResponseMapper;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceResponseRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationEoiEvidenceResponseServiceImpl extends BaseTransactionalService implements ApplicationEoiEvidenceResponseService{

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationEoiEvidenceResponseRepository applicationEoiEvidenceResponseRepository;

    @Autowired
    private ApplicationEoiEvidenceResponseMapper applicationEoiEvidenceResponseMapper;

    @Override
    public ServiceResult<ApplicationEoiEvidenceResponseResource> create(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource) {
        Long applicationId = applicationEoiEvidenceResponseResource.getApplicationId();
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn((application) -> {
                    ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource);
                    return applicationEoiEvidenceResponseMapper.mapToResource(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse));
                });
    }
}
