package com.worth.ifs.application.transactional;

import com.worth.ifs.application.mapper.ApplicationStatusMapper;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
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
