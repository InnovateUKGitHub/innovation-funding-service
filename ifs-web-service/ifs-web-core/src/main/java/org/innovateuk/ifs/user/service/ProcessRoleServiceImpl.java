package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.service.Futures.adapt;

/**
 * This class contains methods to retrieve and store {@link ProcessRoleResource} related data,
 * through the RestService {@link UserRestService}.
 */
@Service
public class ProcessRoleServiceImpl implements ProcessRoleService {

    @Autowired
    private UserRestService userRestService;

    @Override
    public Future<List<ProcessRoleResource>> findAssignableProcessRoles(Long applicationId) {
        return adapt(userRestService.findAssignableProcessRoles(applicationId), re -> asList(re.getSuccess()));
    }

    @Override
    public Future<ProcessRoleResource> getById(Long id) {
        return adapt(userRestService.findProcessRoleById(id), RestResult::getSuccess);
    }
}