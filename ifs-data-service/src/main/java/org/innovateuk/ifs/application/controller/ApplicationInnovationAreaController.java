package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationInnovationAreaService;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that exposes functionality for linking an {@link Application} to an {@link InnovationArea}.
 */
@RestController
@RequestMapping("/applicationInnovationArea")
public class ApplicationInnovationAreaController {

    @Autowired
    private ApplicationInnovationAreaService applicationInnovationAreaService;

    @Autowired
    private ApplicationMapper applicationMapper;

    @PostMapping("/innovationArea/{applicationId}")
    public RestResult<ApplicationResource> setInnovationArea(@PathVariable("applicationId") final Long applicationId, @RequestBody Long innovationAreaId) {
        return applicationInnovationAreaService.setInnovationArea(applicationId, innovationAreaId).toGetResponse();
    }

    @PostMapping("/noInnovationAreaApplicable/{applicationId}")
    public RestResult<ApplicationResource> setNoInnovationAreaApplies(@PathVariable("applicationId") final Long applicationId) {
        return applicationInnovationAreaService.setNoInnovationAreaApplies(applicationId).toGetResponse();
    }

    @GetMapping("/availableInnovationAreas/{applicationId}")
    public RestResult<List<InnovationAreaResource>> getAvailableInnovationAreas(@PathVariable("applicationId") final Long applicationId) {
        return applicationInnovationAreaService.getAvailableInnovationAreas(applicationId).toGetResponse();
    }
}
