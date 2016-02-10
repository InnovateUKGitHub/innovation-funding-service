package com.worth.ifs.application.transactional;


import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface ResponseService {

    @NotSecured("TODO")
    ServiceResult<List<Response>> findResponsesByApplication(final Long applicationId);
}
