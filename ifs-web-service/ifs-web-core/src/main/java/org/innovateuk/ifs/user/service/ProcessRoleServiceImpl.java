package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.application.service.Futures.adapt;

/**
 * This class contains methods to retrieve and store {@link ProcessRoleResource} related data,
 * through the RestService {@link UserRestService}.
 */
@Service
public class ProcessRoleServiceImpl implements ProcessRoleService {

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Override
    public List<ProcessRoleResource> findAssignableProcessRoles(Long applicationId) {
        return processRoleRestService.findAssignableProcessRoles(applicationId).getSuccess();
    }

    @Override
    public Future<ProcessRoleResource> getById(Long id) {
        return adapt(processRoleRestService.findProcessRoleById(id), RestResult::getSuccess);
    }
}