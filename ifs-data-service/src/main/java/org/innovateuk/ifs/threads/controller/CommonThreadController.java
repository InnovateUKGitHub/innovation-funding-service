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

public abstract class CommonThreadController<R> {
    private final ThreadService<R, PostResource> service;

    public CommonThreadController(ThreadService<R, PostResource> service) {
        this.service = service;
    }

    @RequestMapping(value = "/all/{contextClassId}", method = GET)
    public RestResult<List<R>> findAll(@PathVariable("contextClassId") final Long contextClassId) {
        return service.findAll(contextClassId).toGetResponse();
    }

    @RequestMapping(value = "/{threadId}", method = GET)
    public RestResult<R> findOne(@PathVariable("threadId") final Long threadId) {
        return service.findOne(threadId).toGetResponse();
    }

    @RequestMapping(value = "", method = POST)
    public RestResult<Long> create(@RequestBody R thread) {
        return service.create(thread).toPostCreateResponse();
    }

    @RequestMapping(value = "/{threadId}/post", method = POST)
    public RestResult<Void> addPost(@RequestBody PostResource post, @PathVariable("threadId") Long threadId) {
        return service.addPost(post, threadId).toPostCreateResponse();
    }
}