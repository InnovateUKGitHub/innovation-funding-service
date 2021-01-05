package org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProcurementMilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;

public abstract class AbstractProcurementMilestoneFormSaver<R extends ProcurementMilestoneResource> {

    @Autowired
    private ProcurementMilestoneRestService<R> service;

    protected ServiceResult<Void> save(ProcurementMilestonesForm form, Function<ProcurementMilestoneForm, R> mapper) {
        return aggregate(form.getMilestones().entrySet().stream()
                .map(e -> saveRow(e.getKey(), e.getValue(), mapper))
                .collect(Collectors.toList()))
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> saveRow(String id, ProcurementMilestoneForm form, Function<ProcurementMilestoneForm, R> mapper) {
        if (id.startsWith(ProcurementMilestonesForm.UNSAVED_ROW_PREFIX)) {
            return service.create(mapper.apply(form)).toServiceResult().andOnSuccessReturnVoid();
        } else {
            return service.update(mapper.apply(form)).toServiceResult();
        }
    }
}
