package org.innovateuk.ifs.project.activitylog.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/activity-log")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
@SecuredBySpring(value = "VIEW_ACTIVITY_LOG", description = "Only project finance users can manage project state")
public class ActivityLogController {


    @GetMapping
    public String viewActivityLog() {
        return "project/activity-log";
    }

}
