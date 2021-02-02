package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;

import java.util.List;

public interface AssessmentPeriodRestService {
    RestResult<Void> updateAssessmentPeriodMilestones(List<MilestoneResource> milestones);
    RestResult<Void> addNewAssessmentPeriod(long competitionId);
}
