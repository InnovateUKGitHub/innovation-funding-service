package org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProcurementMilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public abstract class AbstractProcurementMilestoneFormSaver<R extends ProcurementMilestoneResource> {

    @Autowired
    protected ProcurementMilestoneRestService<R> service;

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

    public ServiceResult<Void> removeRowFromForm(ProcurementMilestonesForm form, String removeId) {
        return removeRow(removeId)
                .andOnSuccessReturnVoid(() ->  form.getMilestones().remove(removeId));
    }

    public ServiceResult<Void> removeRow(String removeId) {
        if (removeId.startsWith(ProcurementMilestonesForm.UNSAVED_ROW_PREFIX)) {
            return serviceSuccess();
        } else {
            return service.delete(Long.parseLong(removeId)).toServiceResult();
        }
    }
    public void addRowForm(ProcurementMilestonesForm form, int index) {
        ProcurementMilestoneForm row = new ProcurementMilestoneForm(index);
        row.setPayment(BigInteger.ZERO);
        form.getMilestones().put(generateUnsavedRowId(), row);
    }

}
