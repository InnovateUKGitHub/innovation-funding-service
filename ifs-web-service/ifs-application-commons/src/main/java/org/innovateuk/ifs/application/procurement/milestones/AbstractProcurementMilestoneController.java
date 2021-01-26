package org.innovateuk.ifs.application.procurement.milestones;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.AbstractProcurementMilestoneFormSaver;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractProcurementMilestoneController {

    protected abstract String getView();

    protected abstract AbstractProcurementMilestoneFormSaver getSaver();

    protected Map<String, ProcurementMilestoneForm> reorderMilestones(Map<String, ProcurementMilestoneForm> map) {
        return map.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().getMonth(), Comparator.nullsLast(Integer::compareTo)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    protected String viewMilestonesPage(Model model, ProcurementMilestonesForm form, UserResource userResource) {
        form.setMilestones(reorderMilestones(form.getMilestones()));
        return getView();
    }

    protected String viewProjectSetupMilestones(Model model, UserResource userResource, ProcurementMilestonesForm form) {
        model.addAttribute("form", form);
        return viewMilestonesPage(model, form, userResource);
    }


    protected String addAjaxRow(Model model) {
        ProcurementMilestonesForm form = new ProcurementMilestonesForm();
        getSaver().addRowForm(form);
        Map.Entry<String, ProcurementMilestoneForm> entry = form.getMilestones().entrySet().stream().findFirst().get();

        model.addAttribute("form", form);
        model.addAttribute("id", entry.getKey());
        model.addAttribute("row", entry.getValue());
        return "application/procurement-milestones :: ajax-milestone-row";
    }
}
