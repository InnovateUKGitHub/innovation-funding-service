package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.UserPageResource;

public interface RoleProfileStatusRestService {
    RestResult<UserPageResource> getAvailableAssessors(String filter, int pageNumber, int pageSize);
    RestResult<UserPageResource> getUnavailableAssessors(String filter, int pageNumber, int pageSize);
    RestResult<UserPageResource> getDisabledAssessors(String filter, int pageNumber, int pageSize);
}