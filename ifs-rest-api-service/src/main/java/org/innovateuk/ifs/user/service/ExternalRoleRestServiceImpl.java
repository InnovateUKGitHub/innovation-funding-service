package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ExternalRoleRestServiceImpl extends BaseRestService implements ExternalRoleRestService {

    private static final String REST_URL = "/user/%d/add-external-role";

    @Override
    public RestResult<Void> addRoleToUser(long userId, Role role) {
        return putWithRestResult(format(REST_URL, userId), role, Void.class);
    }
}
