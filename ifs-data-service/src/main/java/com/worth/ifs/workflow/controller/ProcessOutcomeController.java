package com.worth.ifs.workflow.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import com.worth.ifs.workflow.transactional.ProcessOutcomeService;
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