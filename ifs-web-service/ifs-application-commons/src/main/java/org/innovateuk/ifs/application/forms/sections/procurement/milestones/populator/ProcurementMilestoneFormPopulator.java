package org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

@Component
public class ProcurementMilestoneFormPopulator {

    public <R extends ProcurementMilestoneResource> ProcurementMilestonesForm populate(List<R> resources) {
        return new ProcurementMilestonesForm(resources.stream()
                .map(ProcurementMilestoneForm::new)
                .collect(toLinkedMap(
                    form -> String.valueOf(form.getId()),
                    Function.identity()
        )));
    }



}
