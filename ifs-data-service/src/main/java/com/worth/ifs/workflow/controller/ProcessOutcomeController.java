package com.worth.ifs.workflow.controller;

import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
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

    @Autowired
    private ProcessOutcomeMapper mapper;

    @RequestMapping("/{id}")
    public ProcessOutcomeResource findById(@PathVariable("id") final Long id) {
        return mapper.mapProcessOutcomeToResource(service.findOne(id));
    }
}