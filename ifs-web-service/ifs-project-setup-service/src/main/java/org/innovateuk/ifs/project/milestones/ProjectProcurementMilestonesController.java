package org.innovateuk.ifs.project.milestones;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.AbstractProcurementMilestoneFormSaver;
import org.innovateuk.ifs.application.procurement.milestones.AbstractProcurementMilestoneController;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.procurement.milestones.populator.ProjectProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProjectProcurementMilestoneViewModelPopulator populator;

    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @Autowired
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    @GetMapping
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    public String viewMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 UserResource userResource,
                                 Model model) {
        model.addAttribute("model", populator.populate(projectId, organisationId, userResource, false, false));
        ProcurementMilestonesForm form = formPopulator.populate(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());
        return viewProjectSetupMilestones(model, userResource, form);
    }

    @Override
    protected String getView() {
        return VIEW;
    }

    // not required for applicants in project setup
    @Override
    protected AbstractProcurementMilestoneFormSaver getSaver() {
        return null;
    }
}