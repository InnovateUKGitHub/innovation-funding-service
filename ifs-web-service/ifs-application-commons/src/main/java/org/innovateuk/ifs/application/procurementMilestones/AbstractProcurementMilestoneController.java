package org.innovateuk.ifs.application.procurementMilestones;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractProcurementMilestoneController {

    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @Autowired
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    protected abstract String getView();

    protected Map<String, ProcurementMilestoneForm> reorderMilestones(Map<String, ProcurementMilestoneForm> map) {
        return map.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().getMonth(), Comparator.nullsLast(Integer::compareTo)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    protected String viewMilestonesPage(Model model, ProcurementMilestonesForm form, UserResource userResource) {
        form.setMilestones(reorderMilestones(form.getMilestones()));
        return getView();
    }

    protected String viewProjectSetupMilestones(Model model, long projectId, long organisationId, UserResource userResource) {
        ProcurementMilestonesForm form = formPopulator.populate(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());
        model.addAttribute("form", form);
        return viewMilestonesPage(model, form, userResource);
    }

}
