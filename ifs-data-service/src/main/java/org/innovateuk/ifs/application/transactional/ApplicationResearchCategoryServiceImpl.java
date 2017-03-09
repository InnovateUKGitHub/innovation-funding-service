package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service implementation for linking an {@link Application} to an {@link InnovationArea}.
 */
@Service
public class ApplicationResearchCategoryServiceImpl extends BaseTransactionalService implements ApplicationResearchCategoryService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ResearchCategoryRepository researchCategoryRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ResearchCategoryMapper researchCategoryMapper;

    @Override
    public ServiceResult<ApplicationResource> setResearchCategory(Long applicationId, Long researchCategoryId) {
        return find(application(applicationId)).andOnSuccess(application ->
                findResearchCategory(researchCategoryId).andOnSuccess(researchCategory ->
                        saveApplicationWithResearchCategory(application, researchCategory))).andOnSuccess(application -> serviceSuccess(applicationMapper.mapToResource(application)));
    }

    private ServiceResult<ResearchCategory> findResearchCategory(Long researchCategoryId) {
        return find(researchCategoryRepository.findById(researchCategoryId), notFoundError(ResearchCategory.class));
    }

    private ServiceResult<Application> saveApplicationWithResearchCategory(Application application, ResearchCategory researchCategory) {
        application.setResearchCategory(researchCategory);
        return serviceSuccess(applicationRepository.save(application));
    }
}
