package org.innovateuk.ifs.project.state.controller;

import org.innovateuk.ifs.project.state.transactional.ProjectStateCommentsService;
import org.innovateuk.ifs.threads.controller.CommonMessageThreadController;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/state/history")
public class ProjectStateCommentsController extends CommonMessageThreadController<ProjectStateCommentsResource> {
    @Autowired
    public ProjectStateCommentsController(ProjectStateCommentsService service) {
        super(service);
    }
}