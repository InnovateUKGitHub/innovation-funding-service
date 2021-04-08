package org.innovateuk.ifs.assessment.period.controller;

import org.innovateuk.ifs.assessment.period.transactional.AssessmentPeriodService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{competitionId}")
    public RestResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionIdAndIndex(
            @PathVariable("competitionId") final long competitionId) {
        return assessmentPeriodService.getAssessmentPeriodByCompetitionId(competitionId).toGetResponse();
    }

    @Override
    protected IfsCrudService<AssessmentPeriodResource, Long> crudService() {
        return assessmentPeriodService;
    }
}
