package org.innovateuk.ifs.horizon.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.transactional.HorizonWorkProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horizon-work-programme")
public class HorizonWorkProgrammeController {

    @Autowired
    private HorizonWorkProgrammeService horizonWorkProgrammeService;

    @GetMapping("/id/{workProgrammeId}")
    public RestResult<HorizonWorkProgrammeResource> findWorkProgramme(@PathVariable final Long workProgrammeId) {
        return horizonWorkProgrammeService.findById(workProgrammeId).toGetResponse();
    }

    @GetMapping("/root")
    public RestResult<List<HorizonWorkProgrammeResource>> findRootWorkProgrammes() {
        return horizonWorkProgrammeService.findRootWorkProgrammes().toGetResponse();
    }

    @GetMapping("/id/{workProgrammeId}/children")
    public RestResult<List<HorizonWorkProgrammeResource>> findChildrenWorkProgrammes(@PathVariable final Long workProgrammeId) {
        return horizonWorkProgrammeService.findChildrenWorkProgrammes(workProgrammeId).toGetResponse();
    }

    @GetMapping("/competition/{competitionId}")
    public RestResult<List<HorizonWorkProgrammeResource>> findWorkProgrammesByCompetition(@PathVariable final Long competitionId) {
        return horizonWorkProgrammeService.findWorkProgrammesByCompetition(competitionId).toGetResponse();
    }

    @PostMapping("/competition/{competitionId}")
    public RestResult<Void> initWorkProgrammeForCompetition(@PathVariable final Long competitionId) {
        return horizonWorkProgrammeService.initWorkProgrammesForCompetition(competitionId).toPostResponse();
    }

    @PostMapping("/update-work-programmes/{applicationId}")
    public RestResult<Void> initWorkProgrammeForCompetition(@PathVariable final Long applicationId,
                                                            @RequestParam List<Long> workProgrammeIds) {
        return horizonWorkProgrammeService.updateWorkProgrammesForApplication(workProgrammeIds, applicationId).toPostResponse();
    }

    @GetMapping("/find-selected/{applicationId}")
    public RestResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(@PathVariable final Long applicationId) {
        return horizonWorkProgrammeService.findSelectedForApplication(applicationId).toGetResponse();
    }
}
