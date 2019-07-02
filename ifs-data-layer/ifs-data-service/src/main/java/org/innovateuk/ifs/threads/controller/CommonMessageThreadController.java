package org.innovateuk.ifs.threads.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.service.MessageThreadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class CommonMessageThreadController<R, S extends MessageThreadService<R, PostResource>> {
    protected final S service;

    public CommonMessageThreadController(S service) {
        this.service = service;
    }

    @GetMapping("/all/{contextClassId}")
    public RestResult<List<R>> findAll(@PathVariable("contextClassId") final Long contextClassId) {
        return service.findAll(contextClassId).toGetResponse();
    }

    @GetMapping("/{threadId}")
    public RestResult<R> findOne(@PathVariable("threadId") final Long threadId) {
        return service.findOne(threadId).toGetResponse();
    }

    @PostMapping
    public RestResult<Long> create(@RequestBody R thread) {
        return service.create(thread).toPostCreateResponse();
    }

    @PostMapping("/thread/{threadId}/close")
    public RestResult<Void> close(@PathVariable("threadId") final Long threadId) {
        return service.close(threadId).toPostResponse();
    }

}