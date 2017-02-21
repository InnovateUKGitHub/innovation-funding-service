package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.transactional.ApplicationInnovationAreaService;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that exposes functionality for linking an {@link Application} to an {@link InnovationArea}.
 */
@RestController
@RequestMapping("/application")
public class ApplicationInnovationAreaController {

    @Autowired
    private ApplicationInnovationAreaService applicationInnovationAreaService;

    @PostMapping("/setInnovationArea/{applicationId}")
    public RestResult<Void> setInnovationArea(@PathVariable("applicationId") final Long applicationId, @RequestBody Long innovationAreaId) {
        return applicationInnovationAreaService.setInnovationArea(applicationId, innovationAreaId).toPostResponse();
    }

    @PostMapping("/setNoInnovationAreaApplies/{applicationId}")
    public RestResult<Void> setNoInnovationAreaApplies(@PathVariable("applicationId") final Long applicationId) {
        return applicationInnovationAreaService.setNoInnovationAreaApplies(applicationId).toPostResponse();
    }

    @GetMapping("/getAvailableInnovationAreas/{applicationId}")
    public RestResult<List<InnovationArea>> getAvailableInnovationAreas(Long applicationId) {
        return applicationInnovationAreaService.getAvailableInnovationAreas(applicationId).toGetResponse();
    }
}
