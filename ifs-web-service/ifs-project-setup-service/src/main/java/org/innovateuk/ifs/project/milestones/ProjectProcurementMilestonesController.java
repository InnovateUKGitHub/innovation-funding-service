package org.innovateuk.ifs.project.milestones;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.milestones.populator.ProjectProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/procurement-milestones")
public class ProjectProcurementMilestonesController {

    private static final String VIEW = "milestones/project-procurement-milestones";

    @Autowired
    private ProjectProcurementMilestoneRestService restService;

    @Autowired
    private ProjectProcurementMilestoneViewModelPopulator viewModelPopulator;

    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @GetMapping
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    public String viewMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 UserResource userResource,
                                 Model model) {
        ProcurementMilestonesForm form = formPopulator.populate(restService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());
        model.addAttribute("form", form);
        return viewMilestones(model, form, projectId, organisationId, userResource);
    }

    private String viewMilestones(Model model, ProcurementMilestonesForm form, long projectId, long organisationId, UserResource userResource) {
        model.addAttribute("model", viewModelPopulator.populate(projectId, organisationId, userResource));
        form.setMilestones(reorderMilestones(form.getMilestones()));
        return VIEW;
    }

    private Map<String, ProcurementMilestoneForm> reorderMilestones(Map<String, ProcurementMilestoneForm> map) {
        return map.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().getMonth(), Comparator.nullsLast(Integer::compareTo)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}