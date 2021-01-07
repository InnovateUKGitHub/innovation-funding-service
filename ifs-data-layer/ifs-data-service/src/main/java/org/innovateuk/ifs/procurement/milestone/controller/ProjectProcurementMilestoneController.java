package org.innovateuk.ifs.procurement.milestone.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProcurementMilestoneService;
import org.innovateuk.ifs.procurement.milestone.transactional.ProjectProcurementMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project-procurement-milestone")
public class ProjectProcurementMilestoneController extends AbstractProcurementMilestoneController<ProjectProcurementMilestoneResource, ProjectProcurementMilestoneId> {

    @Autowired
    private ProjectProcurementMilestoneService projectProcurementMilestoneService;

    @Override
    protected ProcurementMilestoneService<ProjectProcurementMilestoneResource, ProjectProcurementMilestoneId> getProcurementMilestoneService() {
        return projectProcurementMilestoneService;
    }

    @Override
    protected ProjectProcurementMilestoneId getId(long id) {
        return ProjectProcurementMilestoneId.of(id);
    }

    @GetMapping("/project/{projectId}/organisation/{organisationId}")
    public RestResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(@PathVariable final long projectId,
                                                                                                 @PathVariable final long organisationId) {
        return projectProcurementMilestoneService.getByProjectIdAndOrganisationId(projectId, organisationId).toGetResponse();
    }
}
