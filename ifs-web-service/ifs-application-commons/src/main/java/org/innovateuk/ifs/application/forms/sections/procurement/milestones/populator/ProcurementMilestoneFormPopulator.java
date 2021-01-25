package org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class ProcurementMilestoneFormPopulator {

    public <R extends ProcurementMilestoneResource> ProcurementMilestonesForm populate(List<R> resources) {
        Map<String, ProcurementMilestoneForm> map = new LinkedHashMap<>();

        IntStream
                .range(0, resources.size())
                .forEach(index -> map.put(
                        String.valueOf(resources.get(index).getId()),
                        new ProcurementMilestoneForm(resources.get(index), index)
            ));
        return new ProcurementMilestonesForm(map);
    }



}
