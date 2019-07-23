package org.innovateuk.ifs.project.queries.controller;

import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.threads.controller.CommonMessageThreadController;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/finance/queries")
public class ProjectFinanceQueriesController extends CommonMessageThreadController<QueryResource, FinanceCheckQueriesService> {

    @Autowired
    public ProjectFinanceQueriesController(FinanceCheckQueriesService service) {
        super(service);
    }
}