package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.transactional.AssessorFeedbackService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessorfeedback")
public class AssessorFeedbackController {

    @Autowired
    private AssessorFeedbackService service;


    @RequestMapping("/{id}")
    public RestResult<AssessorFeedbackResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toGetResponse();
    }

    @RequestMapping("/findByAssessor/{id}")
    public RestResult<AssessorFeedbackResource> findByAssessorId(@PathVariable("id") final Long assessorId) {
        return service.findByAssessorId(assessorId).toGetResponse();
    }
}