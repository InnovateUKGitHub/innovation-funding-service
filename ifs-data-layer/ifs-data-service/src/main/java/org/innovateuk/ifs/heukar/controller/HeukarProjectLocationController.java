package org.innovateuk.ifs.heukar.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.resource.ApplicationHeukarLocationResource;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;
import org.innovateuk.ifs.heukar.transactional.ApplicationHeukarLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/heukar-project-location")
public class HeukarProjectLocationController {

    @Autowired
    private ApplicationHeukarLocationService applicationHeukarLocationService;

    @PostMapping("/update-locations/{applicationId}")
    public RestResult<Void> updateLocationsForApplication(@PathVariable final long applicationId,
                                                          @RequestParam List<HeukarLocation> locations) {
        return applicationHeukarLocationService.updateLocationsForApplication(locations, applicationId).toPostResponse();

    }

    @GetMapping("/find-selected/{applicationId}")
    public RestResult<List<ApplicationHeukarLocationResource>> findSelectedForApplication(@PathVariable final long applicationId) {
        return applicationHeukarLocationService.findSelectedForApplication(applicationId).toGetResponse();
    }
}


