package org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProcurementMilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public abstract class AbstractProcurementMilestoneFormSaver<R extends ProcurementMilestoneResource> {

    @Autowired
    protected ProcurementMilestoneRestService<R> service;

    protected ServiceResult<Void> save(ProcurementMilestonesForm form, Function<ProcurementMilestoneForm, R> mapper) {
        List<Entry<String, ProcurementMilestoneForm>> entries = new ArrayList<>(form.reorderMilestones().getMilestones().entrySet());
        return aggregate(IntStream.range(0, entries.size())
                .mapToObj(i -> saveRow(entries.get(i).getKey(), entries.get(i).getValue(), mapper, i))
                .collect(Collectors.toList()))
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> saveRow(String id, ProcurementMilestoneForm form, Function<ProcurementMilestoneForm, R> mapper, int index) {
        R resource = mapper.apply(form);
        if (isNullOrEmpty(resource.getDescription())) {
            resource.setDescription("Milestone " + (index + 1));
        }
        if (id.startsWith(ProcurementMilestonesForm.UNSAVED_ROW_PREFIX)) {
            return service.create(resource).toServiceResult().andOnSuccessReturnVoid();
        } else {
            return service.update(resource).toServiceResult();
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
    public void addRowForm(ProcurementMilestonesForm form) {
        ProcurementMilestoneForm row = new ProcurementMilestoneForm();
        form.getMilestones().put(generateUnsavedRowId(), row);
    }

    protected abstract ProcurementMilestoneResource getResource(String id, long targetId, long organisationId);

    protected String idFromRowPath(String field) {
        return field.substring(field.indexOf('[') + 1, field.indexOf(']'));
    }

    protected String fieldFromRowPath(String field) {
        return field.substring(field.indexOf("].") + 2);
    }

}
