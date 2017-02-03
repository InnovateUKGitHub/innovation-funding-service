package org.innovateuk.ifs.threads.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

public class CommonThreadController<R> {
    private final ThreadService<R, PostResource> service;

    public CommonThreadController(ThreadService<R, PostResource> service) {
        this.service = service;
    }

    @RequestMapping(value = "", method = GET)
    public RestResult<List<R>> allThreads(@PathVariable("contextClassId") final Long projectFinanceId) {
        return service.findAll(projectFinanceId).toGetResponse();
    }

    @RequestMapping(value = "", method = POST)
    public RestResult<Void> createThread(@RequestBody R query) {
        return service.create(query).toPostCreateResponse();
    }

    @RequestMapping(value = "/{threadId}/post", method = POST)
    public RestResult<Void> addPost(@RequestBody PostResource post, @PathVariable("queryId") Long queryId) {
        return service.addPost(post, queryId).toPostCreateResponse();
    }
}
