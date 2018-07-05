package org.innovateuk.ifs.threads.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class CommonThreadController<R> {
    private final ThreadService<R, PostResource> service;

    public CommonThreadController(ThreadService<R, PostResource> service) {
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

    @PostMapping("/{threadId}/post")
    public RestResult<Void> addPost(@RequestBody PostResource post, @PathVariable("threadId") Long threadId) {
        return service.addPost(post, threadId).toPostCreateResponse();
    }
}