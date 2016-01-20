package com.worth.ifs.application.controller;

import com.worth.ifs.application.mapper.ApplicationStatusMapper;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.application.transactional.ApplicationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ExposesResourceFor(ApplicationStatusResource.class)
@RequestMapping("/applicationstatus")
public class ApplicationStatusController {

    @Autowired
    ApplicationStatusService applicationStatusService;

    @Autowired
    ApplicationStatusMapper applicationStatusMapper;

    @RequestMapping("/{id}")
    public ApplicationStatusResource getApplicationStatusById(@PathVariable("id") final Long id) {
        return applicationStatusMapper.mapApplicationStatusToResource(applicationStatusService.getById(id));

    }
}
