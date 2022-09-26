package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
public class ApplicationEoiEvidenceResponseController {

    @Autowired
    private ApplicationEoiEvidenceResponseService applicationEoiEvidenceResponseService;

    @GetMapping("/{applicationId}/eoi-evidence-response")
    public RestResult<ApplicationEoiEvidenceResponseResource> findOneByApplicationId(@PathVariable("applicationId") long applicationId) {
        return applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).toGetResponse();
    }
}
