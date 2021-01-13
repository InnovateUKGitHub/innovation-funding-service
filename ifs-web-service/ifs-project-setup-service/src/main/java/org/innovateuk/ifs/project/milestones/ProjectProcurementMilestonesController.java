package org.innovateuk.ifs.project.milestones;

import org.innovateuk.ifs.application.ProcurementMilestones.AbstractProcurementMilestoneController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/procurement-milestones")
public class ProjectProcurementMilestonesController extends AbstractProcurementMilestoneController {

    private static final String VIEW = "milestones/project-procurement-milestones";

    @GetMapping
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    public String viewMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 UserResource userResource,
                                 Model model) {
        return viewProjectSetupMilestones(model, projectId, organisationId, userResource, false);
    }

    @Override
    protected String getView() {
        return VIEW;
    }
}