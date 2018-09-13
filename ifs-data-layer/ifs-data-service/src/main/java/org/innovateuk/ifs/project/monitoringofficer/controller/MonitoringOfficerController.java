package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.transactional.MonitoringOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * ProjectMonitoringOfficerController exposes Monitoring Officer Project data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class MonitoringOfficerController {

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @GetMapping("/{projectId}/monitoring-officer")
    public RestResult<MonitoringOfficerResource> getMonitoringOfficer(@PathVariable("projectId") final Long projectId) {
        return monitoringOfficerService.getMonitoringOfficer(projectId).toGetResponse();
    }

    @PutMapping("/{projectId}/monitoring-officer")
    public RestResult<Void> saveMonitoringOfficer(@PathVariable("projectId") final Long projectId,
                                                  @RequestBody @Valid final MonitoringOfficerResource monitoringOfficerResource) {

        ServiceResult<Boolean> result = monitoringOfficerService.saveMonitoringOfficer(projectId, monitoringOfficerResource)
                .andOnSuccessReturn(r -> r.isMonitoringOfficerSaved());

        if (result.isSuccess() && result.getSuccess()) {
            return monitoringOfficerService.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource).toPutResponse();
        }

        return result.toPutResponse();
    }
}
