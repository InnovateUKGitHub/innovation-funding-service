package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.application.transactional.ApplicationExternalConfigService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application-external-config")
public class ApplicationExternalConfigController {

    @Autowired
    private ApplicationExternalConfigService applicationExternalConfigService;

    @GetMapping("/{applicationId}")
    public RestResult<ApplicationExternalConfigResource> findOneByApplicationId(@PathVariable final long applicationId) {
        return applicationExternalConfigService.findOneByApplicationId(applicationId).toGetResponse();
    }

    @PutMapping("/{applicationId}")
    public RestResult<Void> update(@PathVariable final long applicationId, @RequestBody ApplicationExternalConfigResource applicationExternalConfigResource) {
        return applicationExternalConfigService.update(applicationId, applicationExternalConfigResource).toPutResponse();
    }
}

