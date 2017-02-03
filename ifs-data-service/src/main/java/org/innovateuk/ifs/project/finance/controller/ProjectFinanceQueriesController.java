package org.innovateuk.ifs.project.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesService;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
@RequestMapping("/project/finance/{projectFinanceId}/queries")
public class ProjectFinanceQueriesController {

    @Autowired
    ProjectFinanceQueriesService service;

    @RequestMapping(value = "", method = GET)
    public RestResult<List<QueryResource>> queries(@PathVariable("projectFinanceId") final Long projectFinanceId) {
        return service.findAll(projectFinanceId).toGetResponse();
    }

    @RequestMapping(value = "", method = POST)
    public RestResult<Void> createQuery(@RequestBody QueryResource query ) {
        return service.create(query).toPostCreateResponse();
    }

    @RequestMapping(value = "/{queryId}/post", method = POST)
    public RestResult<Void> addPost(@PathVariable("queryId") Long queryId, @RequestBody PostResource post ) {
        return service.addPost(post, queryId).toPostCreateResponse();
    }
}
