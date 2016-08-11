package com.worth.ifs.project.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.transactional.ProjectFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * ProjectFinanceController exposes Project finance data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectFinanceController {

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @RequestMapping(value = "/{projectId}/spend-profile/generate", method = POST)
    public RestResult<Void> generateSpendProfile(@PathVariable("projectId") final Long projectId) {
        return projectFinanceService.generateSpendProfile(projectId).toPostCreateResponse();
    }
}
