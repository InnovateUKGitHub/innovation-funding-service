package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.service.ThreadRestService;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectFinanceQueriesRestService extends ThreadRestService<QueryResource> {

    public ProjectFinanceQueriesRestService() {
        super("/project/finance/queries", QueryResource.class, new ParameterizedTypeReference<List<QueryResource>>(){});
    }

}
