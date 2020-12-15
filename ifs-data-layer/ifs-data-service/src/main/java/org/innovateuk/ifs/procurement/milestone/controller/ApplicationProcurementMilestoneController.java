package org.innovateuk.ifs.procurement.milestone.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.transactional.ApplicationProcurementMilestoneService;
import org.innovateuk.ifs.procurement.milestone.transactional.ProcurementMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/application-procurement-milestone")
public class ApplicationProcurementMilestoneController extends AbstractProcurementMilestoneController<ApplicationProcurementMilestoneResource> {

    @Autowired
    private ApplicationProcurementMilestoneService applicationProcurementMilestoneService;

    @Override
    protected ProcurementMilestoneService<ApplicationProcurementMilestoneResource> getProcurementMilestoneService() {
        return applicationProcurementMilestoneService;
    }

    @Override
    protected ProcurementMilestoneId getId(long id) {
        return ApplicationProcurementMilestoneId.of(id);
    }

    @GetMapping("application/{applicationId}/organisation/{organisationId}")
    public RestResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(@PathVariable final long applicationId,
                                                                                                         @PathVariable final long organisationId) {
        return applicationProcurementMilestoneService.getByApplicationIdAndOrganisationId(applicationId, organisationId).toGetResponse();
    }
}
