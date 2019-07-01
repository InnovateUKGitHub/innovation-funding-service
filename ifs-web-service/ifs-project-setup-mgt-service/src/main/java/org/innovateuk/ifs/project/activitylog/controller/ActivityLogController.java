package org.innovateuk.ifs.project.activitylog.controller;


import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.service.ActivityLogRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/activity-log")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
@SecuredBySpring(value = "VIEW_ACTIVITY_LOG", description = "Only project finance users can manage project state")
public class ActivityLogController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ActivityLogRestService activityLogRestService;

    @GetMapping
    public String viewActivityLog(@PathVariable long projectId,
                                  Model model) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        List<ActivityLogResource> activities = activityLogRestService.findByApplicationId(project.getApplication()).getSuccess();
        model.addAttribute("activities", activities);
        return "project/activity-log";
    }

}
