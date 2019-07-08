package org.innovateuk.ifs.project.state.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.state.transactional.ProjectStateCommentsService;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}/state/comments")
public class ProjectStateCommentsController {

    @Autowired
    private ProjectStateCommentsService projectStateCommentsService;

    @GetMapping("/open")
    public RestResult<ProjectStateCommentsResource> findOne(@PathVariable final Long projectId) {
        return projectStateCommentsService.findOpenComment(projectId).toGetResponse();
    }

    @PostMapping("/{commentId}/post")
    public RestResult<Void> addPost(@RequestBody PostResource post, @PathVariable long commentId) {
        return projectStateCommentsService.addPost(post, commentId).toPostCreateResponse();
    }
}