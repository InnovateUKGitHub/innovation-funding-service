package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentPanelService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessmentpanel")
public class AssessmentPanelController {

    @Autowired
    private AssessmentPanelService assessmentPanelService;

    @PostMapping("/assignApplication/{applicationId}")
    public RestResult<Void> assignApplication(@PathVariable long applicationId) {
        return assessmentPanelService.assignApplicationToPanel(applicationId).toPostResponse();
    }

    @PostMapping("/unassignApplication/{applicationId}")
    public RestResult<Void> unAssignApplication(@PathVariable long applicationId) {
        return assessmentPanelService.unassignApplicationFromPanel(applicationId).toPostResponse();
    }
}
