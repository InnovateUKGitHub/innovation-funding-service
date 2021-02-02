package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.transactional.AssessmentPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessment-period")
public class AssessmentPeriodController {

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    @PutMapping("/update")
    public RestResult<Void> updateAssessmentPeriodMilestones(@RequestBody final List<MilestoneResource> milestones) {
        return assessmentPeriodService.updateAssessmentPeriodMilestones(milestones).toPutResponse();
    }

    @PostMapping("/{competitionId}/new")
    public RestResult<List<MilestoneResource>> newAssessmentPeriod(@PathVariable final long competitionId) {
        return assessmentPeriodService.createAssessmentPeriodMilestones(competitionId).toPostCreateResponse();
    }
}
