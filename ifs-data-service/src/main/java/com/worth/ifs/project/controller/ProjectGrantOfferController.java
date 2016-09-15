package com.worth.ifs.project.controller;

import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Module: innovation-funding-service-dev
 * Project Controller extension for grant offer data and operations through a REST API
 **/
@RestController
@RequestMapping("/project")
public class ProjectGrantOfferController {

    @Autowired
    private ProjectService projectService;

}
