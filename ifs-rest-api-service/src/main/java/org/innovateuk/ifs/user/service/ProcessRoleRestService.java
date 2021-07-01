package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.concurrent.Future;

public interface ProcessRoleRestService {

    RestResult<ProcessRoleResource> findProcessRole(long userId, long applicationId);

    RestResult<List<ProcessRoleResource>> findProcessRole(long applicationId);

    RestResult<List<ProcessRoleResource>> findProcessRoleByUserId(long userId);

    RestResult<List<ProcessRoleResource>> findAssignableProcessRoles(long applicationId);

    RestResult<Boolean> userHasApplicationForCompetition(long userId, long competitionId);

    Future<RestResult<ProcessRoleResource>> findProcessRoleById(long processRoleId);
}
