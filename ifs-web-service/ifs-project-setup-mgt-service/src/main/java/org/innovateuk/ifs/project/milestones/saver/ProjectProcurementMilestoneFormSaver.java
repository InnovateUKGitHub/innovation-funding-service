package org.innovateuk.ifs.project.milestones.saver;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.AbstractProcurementMilestoneFormSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.springframework.stereotype.Component;

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
}
