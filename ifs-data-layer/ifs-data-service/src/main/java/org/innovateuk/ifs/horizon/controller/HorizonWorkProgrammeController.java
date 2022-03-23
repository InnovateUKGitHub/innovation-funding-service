package org.innovateuk.ifs.horizon.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.transactional.HorizonWorkProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horizon-work-programme")
public class HorizonWorkProgrammeController {

    @Autowired
    private HorizonWorkProgrammeService horizonWorkProgrammeService;

    @PostMapping("/update-work-programmes/{applicationId}")
    public RestResult<Void> updateWorkProgrammeForApplication(@PathVariable final long applicationId,
                                                              @RequestParam List<HorizonWorkProgramme> workProgrammes) {
        return horizonWorkProgrammeService.updateWorkProgrammesForApplication(workProgrammes, applicationId).toPostResponse();
    }

    @GetMapping("/find-selected/{applicationId}")
    public RestResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(@PathVariable final long applicationId) {
        return horizonWorkProgrammeService.findSelectedForApplication(applicationId).toGetResponse();
    }
}
