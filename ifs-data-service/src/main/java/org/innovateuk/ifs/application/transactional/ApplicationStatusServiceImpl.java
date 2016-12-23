package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.mapper.ApplicationStatusMapper;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationStatusServiceImpl extends BaseTransactionalService implements ApplicationStatusService {

    @Autowired
    private ApplicationStatusMapper mapper;

    @Override
    public ServiceResult<ApplicationStatusResource> getById(Long id) {
        return getApplicationStatus(id).andOnSuccessReturn(mapper::mapToResource);
    }
}
