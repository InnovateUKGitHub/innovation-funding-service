package org.innovateuk.ifs.assessment.period.controller;

import org.innovateuk.ifs.assessment.period.transactional.AssessmentPeriodService;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AssessmentPeriodController exposes AssessmentPeriod data and operations through a REST API
 */
@RestController
@RequestMapping("/assessment-period")
public class AssessmentPeriodController extends AbstractCrudController<AssessmentPeriodResource, Long> {

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    @GetMapping(params = "competitionId")
    public RestResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(
            @RequestParam final long competitionId) {
        return assessmentPeriodService.getAssessmentPeriodByCompetitionId(competitionId).toGetResponse();
    }
    @GetMapping(params = {"competitionId", "page", "size"})
    public RestResult<PageResource<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(
            @RequestParam final long competitionId,
            @RequestParam final int page,
            @RequestParam final int size) {
        return assessmentPeriodService.getAssessmentPeriodByCompetitionId(competitionId, PageRequest.of(page, size)).toGetResponse();
    }

    @Override
    protected IfsCrudService<AssessmentPeriodResource, Long> crudService() {
        return assessmentPeriodService;
    }
}
