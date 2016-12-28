package org.innovateuk.ifs.workflow.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.innovateuk.ifs.workflow.transactional.ProcessOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processoutcome")
public class ProcessOutcomeController {
    @Autowired
    private ProcessOutcomeService service;

    @RequestMapping("/{id}")
    public RestResult<ProcessOutcomeResource> findById(@PathVariable("id") Long id) {
        return service.findOne(id).toGetResponse();
    }

    @RequestMapping("/process/{id}")
    public RestResult<ProcessOutcomeResource> findLatestByProcess(@PathVariable("id") Long id) {
        return service.findLatestByProcess(id).toGetResponse();
    }

    @RequestMapping("/process/{id}/type/{type}")
    public RestResult<ProcessOutcomeResource> findLatestByProcessAndOutcomeType(@PathVariable("id") Long id, @PathVariable("type") String type) {
        return service.findLatestByProcessAndOutcomeType(id, type).toGetResponse();
    }
}
