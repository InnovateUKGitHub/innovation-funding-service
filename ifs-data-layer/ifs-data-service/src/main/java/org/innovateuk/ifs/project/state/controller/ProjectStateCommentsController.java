package org.innovateuk.ifs.project.state.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.state.transactional.ProjectStateCommentsService;
import org.innovateuk.ifs.threads.controller.CommonMessageThreadController;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/state/comments")
public class ProjectStateCommentsController extends CommonMessageThreadController<ProjectStateCommentsResource, ProjectStateCommentsService> {

    @Autowired
    public ProjectStateCommentsController(ProjectStateCommentsService service) {
        super(service);
    }

    @GetMapping("/open/{projectId}")
    public RestResult<ProjectStateCommentsResource> findOne(@PathVariable final Long projectId) {
        return service.findOpenComment(projectId).toGetResponse();
    }
}