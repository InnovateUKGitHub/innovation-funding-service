package org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Optional;


@Component
public class ApplicationProcurementMilestoneFormSaver extends AbstractProcurementMilestoneFormSaver<ApplicationProcurementMilestoneResource> {

    public ServiceResult<Void> save(ProcurementMilestonesForm form, long applicationId, long organisationId) {
        return save(form, row -> {
            ApplicationProcurementMilestoneResource resource =  new ApplicationProcurementMilestoneResource();
            resource.setApplicationId(applicationId);
            resource.setOrganisationId(organisationId);
            row.copyToResource(resource);
            return resource;
        });
    }

    public Optional<Long> autoSave(String field, String value, long applicationId, long organisationId) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        ApplicationProcurementMilestoneResource milestone = getResource(id, applicationId, organisationId);

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

    private ApplicationProcurementMilestoneResource getResource(String id, long applicationId, long organisationId) {
        if (id.startsWith(ProcurementMilestonesForm.UNSAVED_ROW_PREFIX)) {
            ApplicationProcurementMilestoneResource toCreate = new ApplicationProcurementMilestoneResource();
            toCreate.setApplicationId(applicationId);
            toCreate.setOrganisationId(organisationId);
            return service.create(toCreate).getSuccess();
        } else {
            return service.get(Long.parseLong(id)).getSuccess();
        }
    }

    private String idFromRowPath(String field) {
        return field.substring(field.indexOf('[') + 1, field.indexOf(']'));
    }

    private String fieldFromRowPath(String field) {
        return field.substring(field.indexOf("].") + 2);
    }
}
