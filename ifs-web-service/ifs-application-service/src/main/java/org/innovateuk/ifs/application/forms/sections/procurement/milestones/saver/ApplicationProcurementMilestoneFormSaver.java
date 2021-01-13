package org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.stereotype.Component;

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
}
