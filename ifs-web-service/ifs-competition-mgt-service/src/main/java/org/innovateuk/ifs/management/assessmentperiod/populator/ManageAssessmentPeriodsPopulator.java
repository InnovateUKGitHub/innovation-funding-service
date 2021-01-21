package org.innovateuk.ifs.management.assessmentperiod.populator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.model.ManageAssessmentPeriodsViewModel;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ManageAssessmentPeriodsPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ManageAssessmentPeriodsViewModel populateModel(long competitionId) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new ManageAssessmentPeriodsViewModel(competitionResource);
    }



}
