package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static com.worth.ifs.application.service.Futures.adapt;
import static java.util.Arrays.asList;

/**
 * This class contains methods to retrieve and store {@link ProcessRoleResource} related data,
 * through the RestService {@link UserRestService}.
 */
// TODO DW - INFUND-1555 - return RestResults from this Service
@Service
public class ProcessRoleServiceImpl implements ProcessRoleService {
    @Autowired
    UserRestService userRestService;

    @Override
    public ProcessRoleResource findProcessRole(Long userId, Long applicationId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<ProcessRoleResource> findProcessRolesByApplicationId(Long applicationId) {
        return userRestService.findProcessRole(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Future<List<ProcessRoleResource>> findAssignableProcessRoles(Long applicationId) {
        return adapt(userRestService.findAssignableProcessRoles(applicationId), re -> asList(re.getSuccessObject()));
    }

    @Override
    public Future<ProcessRoleResource> getById(Long id) {
        return adapt(userRestService.findProcessRoleById(id), RestResult::getSuccessObjectOrThrowException);
    }

    @Override
    public List<ProcessRoleResource> getByApplicationId(Long applicationId) {
        return userRestService.findProcessRole(applicationId).getSuccessObjectOrThrowException();
    }
}
