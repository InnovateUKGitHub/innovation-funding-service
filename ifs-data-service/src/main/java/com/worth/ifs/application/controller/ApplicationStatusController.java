package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.application.transactional.ApplicationStatusService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

@RestController
@ExposesResourceFor(ApplicationStatusResource.class)
@RequestMapping("/applicationstatus")
public class ApplicationStatusController {

    @Autowired
    private ApplicationStatusService applicationStatusService;

    @RequestMapping("/{id}")
    public RestResult<ApplicationStatusResource> getApplicationStatusById(@PathVariable("id") final Long id) {
        return newRestHandler(ApplicationStatusResource.class).perform(() -> applicationStatusService.getById(id));
    }
}
