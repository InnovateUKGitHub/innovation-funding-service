package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MilestoneController exposes Milestone data and operations through a REST API
 */
@RestController
@RequestMapping("/milestone")
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    @GetMapping("/{competitionId}/public")
    public RestResult<List<MilestoneResource>> getAllPublicMilestonesByCompetitionId(
            @PathVariable("competitionId") final Long competitionId) {
        return milestoneService.getAllPublicMilestonesByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/{competitionId}")
    public RestResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(
            @PathVariable("competitionId") final Long competitionId) {
        return milestoneService.getAllMilestonesByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/{competitionId}/get-by-type")
    public RestResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(@RequestParam("type") final MilestoneType type,
                                                                            @PathVariable("competitionId") final Long competitionId) {
        return milestoneService.getMilestoneByTypeAndCompetitionId(type, competitionId).toGetResponse();
    }

    @PostMapping("/{competitionId}")
    public RestResult<MilestoneResource> create(@RequestParam("type") final MilestoneType type,
                                                @PathVariable("competitionId") final Long competitionId) {
        return milestoneService.create(type, competitionId).toPostCreateResponse();
    }

    @PutMapping("/many")
    public RestResult<Void> saveMilestones(@RequestBody final List<MilestoneResource> milestones) {
        return milestoneService.updateMilestones(milestones).toPutResponse();
    }

    @PutMapping("/assessment-periods")
    public RestResult<Void> newAssessmentPeriod(@RequestBody final List<MilestoneResource> milestones) {
        return milestoneService.updateAssessmentPeriodMilestones(milestones).toPutResponse();
    }

    @PostMapping("/{competitionId}/new-assessment-period")
    public RestResult<List<MilestoneResource>> newAssessmentPeriod(@PathVariable final long competitionId) {
        return milestoneService.createAssessmentPeriodMilestones(competitionId).toPostCreateResponse();
    }

    @PutMapping("/")
    public RestResult<Void> saveMilestone(@RequestBody final MilestoneResource milestone) {
        return milestoneService.updateMilestone(milestone).toPutResponse();
    }

    @PutMapping("/competition/{competitionId}/completion-stage")
    public RestResult<Void> updateCompletionStage(@PathVariable("competitionId") long competitionId,
                                                  @RequestParam("completionStage") final CompetitionCompletionStage completionStage) {

        return milestoneService.updateCompletionStage(competitionId, completionStage).toPutResponse();
    }
}