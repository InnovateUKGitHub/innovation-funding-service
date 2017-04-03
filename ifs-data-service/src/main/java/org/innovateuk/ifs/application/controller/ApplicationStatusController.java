package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.application.transactional.ApplicationStatusService;
import org.innovateuk.ifs.commons.rest.RestResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applicationstatus")
public class ApplicationStatusController {

    @Autowired
    private ApplicationStatusService applicationStatusService;

    @GetMapping("/{id}")
    public RestResult<ApplicationStatusResource> getApplicationStatusById(@PathVariable("id") final Long id) {
        return applicationStatusService.getById(id).toGetResponse();
    }
}
