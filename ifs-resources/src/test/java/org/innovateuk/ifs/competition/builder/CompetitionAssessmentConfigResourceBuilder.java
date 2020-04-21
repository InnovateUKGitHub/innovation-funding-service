package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionAssessmentConfigResourceBuilder extends BaseBuilder<CompetitionAssessmentConfigResource, CompetitionAssessmentConfigResourceBuilder> {

    private CompetitionAssessmentConfigResourceBuilder (List<BiConsumer<Integer, CompetitionAssessmentConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionAssessmentConfigResourceBuilder newCompetitionAssessmentConfigResource() {
        return new CompetitionAssessmentConfigResourceBuilder(emptyList());
    }

    public CompetitionAssessmentConfigResourceBuilder withAverageAssessorScore(Boolean... averageAssessorScore) {
        return withArraySetFieldByReflection("averageAssessorScore", averageAssessorScore);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessorCount(Integer... assessorCount) {
        return withArraySetFieldByReflection("assessorCount", assessorCount);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessorPay(BigDecimal... assessorPay) {
        return withArraySetFieldByReflection("assessorPay", assessorPay);
    }

    public CompetitionAssessmentConfigResourceBuilder withHasAssessmentPanel(Boolean... hasAssessmentPanel) {
        return withArraySetFieldByReflection("hasAssessmentPanel", hasAssessmentPanel);
    }

    public CompetitionAssessmentConfigResourceBuilder withHasInterviewStage(Boolean... hasInterviewStage) {
        return withArraySetFieldByReflection("hasInterviewStage", hasInterviewStage);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessorFinanceView(AssessorFinanceView... assessorFinanceView) {
        return withArraySetFieldByReflection("assessorFinanceView", assessorFinanceView);
    }

    @Override
    protected CompetitionAssessmentConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionAssessmentConfigResource>> actions) {
        return new CompetitionAssessmentConfigResourceBuilder(actions);
    }

    @Override
    protected CompetitionAssessmentConfigResource createInitial() {
        return new CompetitionAssessmentConfigResource();
    }
}