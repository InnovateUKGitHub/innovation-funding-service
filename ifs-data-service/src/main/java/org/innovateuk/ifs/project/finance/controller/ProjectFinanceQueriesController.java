package org.innovateuk.ifs.project.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesService;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
@RequestMapping("/project/finance/{projectFinanceId}/queries")
public class ProjectFinanceQueriesController {

    @Autowired
    ProjectFinanceQueriesService service;

    @RequestMapping(value = "", method = GET)
    public RestResult<Query> queries(@PathVariable("projectFinanceId") final Long projectFinanceId) {
        return service.queries(projectFinanceId).toGetResponse();
    }

    @RequestMapping(value = "", method = POST)
    public RestResult<Query> createQuery(@RequestBody Query query ) {
        return service.createQuery(query).toGetResponse();
    }

    @RequestMapping(value = "/post", method = POST)
    public RestResult<Post> createPost(@RequestBody Post post ) {
        return service.createPost(post).toGetResponse();
    }




}
