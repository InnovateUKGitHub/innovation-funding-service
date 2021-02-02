package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentPeriodRestServiceImpl extends BaseRestService implements AssessmentPeriodRestService {

    private String assessmentPeriodRestUrl = "/assessment-period";

    @Override
    public RestResult<Void> addNewAssessmentPeriod(long competitionId) {
        return postWithRestResult(assessmentPeriodRestUrl + "/" + competitionId + "/new");
    }

    @Override
    public RestResult<Void> updateAssessmentPeriodMilestones(List<MilestoneResource> milestones) {
        return putWithRestResult(assessmentPeriodRestUrl + "/update", milestones, Void.class);
    }
}

