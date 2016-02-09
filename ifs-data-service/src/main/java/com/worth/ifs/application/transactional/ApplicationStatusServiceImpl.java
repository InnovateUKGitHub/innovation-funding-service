package com.worth.ifs.application.transactional;

import com.worth.ifs.application.mapper.ApplicationStatusMapper;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationStatusServiceImpl extends BaseTransactionalService implements ApplicationStatusService {

    @Autowired
    private ApplicationStatusMapper applicationStatusMapper;

    @Override
    public ServiceResult<ApplicationStatusResource> getById(Long id) {
        return super.getApplicationStatus(id).andOnSuccess(status ->
            serviceSuccess(applicationStatusMapper.mapApplicationStatusToResource(status))
        );
    }
}
