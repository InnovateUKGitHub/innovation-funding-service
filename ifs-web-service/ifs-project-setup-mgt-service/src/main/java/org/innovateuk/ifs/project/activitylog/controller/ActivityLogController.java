package org.innovateuk.ifs.project.activitylog.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.activitylog.populator.ActivityLogViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/activity-log")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
@SecuredBySpring(value = "VIEW_ACTIVITY_LOG", description = "Only project finance users can view activity log")
public class ActivityLogController {

    @Autowired
    private ActivityLogViewModelPopulator activityLogViewModelPopulator;

    @GetMapping
    public String viewActivityLog(@PathVariable long projectId,
                                  Model model) {
        model.addAttribute("model", activityLogViewModelPopulator.populate(projectId));
        return "project/activity-log";
    }

}
