package com.worth.ifs.application.controller;

import com.worth.ifs.application.mapper.AssessorFeedbackMapper;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.transactional.AssessorFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessorfeedback")
public class AssessorFeedbackController {
    @Autowired
    private AssessorFeedbackService service;

    @Autowired
    private AssessorFeedbackMapper mapper;

    @RequestMapping("/{id}")
    public AssessorFeedbackResource findById(@PathVariable("id") final Long id) {
        return mapper.mapAssessorFeedbackToResource(service.findOne(id));
    }
}