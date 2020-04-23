package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionAssessmentConfigBuilder extends BaseBuilder<CompetitionAssessmentConfig, CompetitionAssessmentConfigBuilder> {

    private CompetitionAssessmentConfigBuilder (List<BiConsumer<Integer, CompetitionAssessmentConfig>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionAssessmentConfigBuilder newCompetitionAssessmentConfig() {
        return new CompetitionAssessmentConfigBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionAssessmentConfigBuilder withAverageAssessorScore(Boolean... averageAssessorScore) {
        return withArraySetFieldByReflection("averageAssessorScore", averageAssessorScore);
    }

    public CompetitionAssessmentConfigBuilder withCompetition(Competition... competition) {
        return withArraySetFieldByReflection("competition", competition);
    }

    public CompetitionAssessmentConfigBuilder withAssessorCount(Integer... assessorCount) {
        return withArraySetFieldByReflection("assessorCount", assessorCount);
    }

    public CompetitionAssessmentConfigBuilder withAssessorPay(BigDecimal... assessorPay) {
        return withArraySetFieldByReflection("assessorPay", assessorPay);
    }

    public CompetitionAssessmentConfigBuilder withHasAssessmentPanel(Boolean... hasAssessmentPanel) {
        return withArraySetFieldByReflection("hasAssessmentPanel", hasAssessmentPanel);
    }

    public CompetitionAssessmentConfigBuilder withHasInterviewStage(Boolean... hasInterviewStage) {
        return withArraySetFieldByReflection("hasInterviewStage", hasInterviewStage);
    }

    public CompetitionAssessmentConfigBuilder withAssessorFinanceView(AssessorFinanceView... assessorFinanceView) {
        return withArraySetFieldByReflection("assessorFinanceView", assessorFinanceView);
    }

    @Override
    protected CompetitionAssessmentConfigBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionAssessmentConfig>> actions) {
        return new CompetitionAssessmentConfigBuilder(actions);
    }

    @Override
    protected CompetitionAssessmentConfig createInitial() {
        return new CompetitionAssessmentConfig();
    }
}
