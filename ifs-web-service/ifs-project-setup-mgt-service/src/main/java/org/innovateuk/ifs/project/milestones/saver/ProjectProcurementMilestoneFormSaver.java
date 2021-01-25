package org.innovateuk.ifs.project.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.AbstractProcurementMilestoneFormSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Optional;

@Component
public class ProjectProcurementMilestoneFormSaver extends AbstractProcurementMilestoneFormSaver<ProjectProcurementMilestoneResource> {

    public ServiceResult<Void> save(ProcurementMilestonesForm form, long projectId, long organisationId) {
        return save(form, row -> {
            ProjectProcurementMilestoneResource resource =  new ProjectProcurementMilestoneResource();
            resource.setProjectId(projectId);
            resource.setOrganisationId(organisationId);
            row.copyToResource(resource);
            return resource;
        });
    }

    public Optional<Long> autoSave(String field, String value, long targetId, long organisationId) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        ProjectProcurementMilestoneResource milestone = getResource(id, targetId, organisationId);

        switch (rowField) {
            case "month":
                milestone.setMonth(Integer.valueOf(value));
                break;
            case "payment":
                milestone.setPayment(new BigInteger(value));
                break;
            case "description":
                milestone.setDescription(value);
                break;
            case "taskOrActivity":
                milestone.setTaskOrActivity(value);
                break;
            case "deliverable":
                milestone.setDeliverable(value);
                break;
            case "successCriteria":
                milestone.setSuccessCriteria(value);
                break;
        }
        service.update(milestone);
        return Optional.of(milestone.getId());
    }


    @Override
    protected ProjectProcurementMilestoneResource getResource(String id, long projectId, long organisationId) {
        if (id.startsWith(ProcurementMilestonesForm.UNSAVED_ROW_PREFIX)) {
            ProjectProcurementMilestoneResource toCreate = new ProjectProcurementMilestoneResource();
            toCreate.setProjectId(projectId);
            toCreate.setOrganisationId(organisationId);
            return service.create(toCreate).getSuccess();
        } else {
            return service.get(Long.parseLong(id)).getSuccess();
        }
    }
}
