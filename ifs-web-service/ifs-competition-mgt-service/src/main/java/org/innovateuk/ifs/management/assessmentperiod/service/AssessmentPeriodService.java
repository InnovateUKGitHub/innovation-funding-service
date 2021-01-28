package org.innovateuk.ifs.management.assessmentperiod.service;

import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;

import java.util.List;

public interface AssessmentPeriodService {

    List<AssessmentPeriodForm> getAssessmentPeriodMilestonesForms(long competitionId);

    List<MilestoneResource> extractMilestoneResourcesFromForm(ManageAssessmentPeriodsForm form, long competitionId);
}
