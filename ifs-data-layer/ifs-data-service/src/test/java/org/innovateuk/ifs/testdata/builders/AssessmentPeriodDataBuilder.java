package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.builder.MilestoneBuilder;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data from Assessment Periods and attaches it to a competition
 */
public class AssessmentPeriodDataBuilder extends BaseDataBuilder<Void, AssessmentPeriodDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentPeriodDataBuilder.class);

    public AssessmentPeriodDataBuilder withCompetitionAssessmentPeriods(String competitionName,
                                                                        ZonedDateTime assessorBriefing,
                                                                        ZonedDateTime assessorAccepts,
                                                                        ZonedDateTime assessorDeadline) {
        return with(data -> {

            testService.doWithinTransaction(() -> {
                Competition competition = retrieveCompetitionByName(competitionName);

                AssessmentPeriod assessmentPeriod = new AssessmentPeriod(competition);
                assessmentPeriodRepository.save(assessmentPeriod);

                milestoneRepository.save(MilestoneBuilder.newMilestone()
                        .withType(MilestoneType.ASSESSOR_BRIEFING)
                        .withDate(assessorBriefing)
                        .withCompetition(competition)
                        .withAssessmentPeriod(assessmentPeriod)
                        .build());

                milestoneRepository.save(MilestoneBuilder.newMilestone()
                        .withType(MilestoneType.ASSESSOR_ACCEPTS)
                        .withDate(assessorAccepts)
                        .withCompetition(competition)
                        .withAssessmentPeriod(assessmentPeriod)
                        .build());

                milestoneRepository.save(MilestoneBuilder.newMilestone()
                        .withType(MilestoneType.ASSESSOR_DEADLINE)
                        .withDate(assessorDeadline)
                        .withCompetition(competition)
                        .withAssessmentPeriod(assessmentPeriod)
                        .build());
            });
        });
    }

    public static AssessmentPeriodDataBuilder newCompetitionAssessmentPeriods(ServiceLocator serviceLocator) {
        return new AssessmentPeriodDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private AssessmentPeriodDataBuilder(List<BiConsumer<Integer, Void>> multiActions, ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected AssessmentPeriodDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new AssessmentPeriodDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Created Competition Assessment Periods");
    }
}
