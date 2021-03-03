package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.processRoleResourceListType;

@Service
public class ProcessRoleRestServiceImpl extends BaseRestService implements ProcessRoleRestService {

    private static final String PROCESS_ROLE_REST_URL = "/processrole";

    @Override
    public RestResult<ProcessRoleResource> findProcessRole(long userId, long applicationId) {
        return getWithRestResult(PROCESS_ROLE_REST_URL + "/find-by-user-application/" + userId + "/" + applicationId, ProcessRoleResource.class);
    }

    @Override
    public Future<RestResult<ProcessRoleResource>> findProcessRoleById(long processRoleId) {
        return getWithRestResultAsync(PROCESS_ROLE_REST_URL + "/" + processRoleId, ProcessRoleResource.class);
    }

    @Override
    public RestResult<List<ProcessRoleResource>> findProcessRole(long applicationId) {
        return getWithRestResult(PROCESS_ROLE_REST_URL + "/find-by-application-id/" + applicationId, processRoleResourceListType());
    }

    @Override
    public RestResult<List<ProcessRoleResource>> findProcessRoleByUserId(long userId) {
        return getWithRestResult(PROCESS_ROLE_REST_URL + "/find-by-user-id/" + userId, processRoleResourceListType());
    }

    @Override
    public RestResult<List<ProcessRoleResource>> findAssignableProcessRoles(long applicationId){
        return getWithRestResult(PROCESS_ROLE_REST_URL + "/find-assignable/" + applicationId, processRoleResourceListType());
    }

    @Override
    public RestResult<Boolean> userHasApplicationForCompetition(long userId, long competitionId) {
        return getWithRestResult(PROCESS_ROLE_REST_URL + "/user-has-application-for-competition/" + userId + "/" + competitionId, Boolean.class);
    }
}
