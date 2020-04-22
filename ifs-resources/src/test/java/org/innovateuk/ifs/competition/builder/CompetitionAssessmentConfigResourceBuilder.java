package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class CompetitionAssessmentConfigResourceBuilder extends BaseBuilder<CompetitionAssessmentConfigResource, CompetitionAssessmentConfigResourceBuilder> {

    private CompetitionAssessmentConfigResourceBuilder(List<BiConsumer<Integer, CompetitionAssessmentConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionAssessmentConfigResourceBuilder newCompetitionAssessmentConfigResource() {
        return new CompetitionAssessmentConfigResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionAssessmentConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionAssessmentConfigResource>> actions) {
        return new CompetitionAssessmentConfigResourceBuilder(actions);
    }

    @Override
    protected CompetitionAssessmentConfigResource createInitial() {
        return new CompetitionAssessmentConfigResource();
    }

    public CompetitionAssessmentConfigResourceBuilder withAverageAssessorScore(Boolean... averageAssessorScore) {
        return withArray((streamName, object) -> setField("averageAssessorScore", streamName, object), averageAssessorScore);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessorCount(Integer... assessorCount) {
        return withArray((streamName, object) -> setField("assessorCount", streamName, object), assessorCount);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessorPay(BigDecimal... assessorPay) {
        return withArray((streamName, object) -> setField("assessorPay", streamName, object), assessorPay);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessmentPanel(Boolean... hasAssessmentPanel) {
        return withArray((streamName, object) -> setField("hasAssessmentPanel", streamName, object), hasAssessmentPanel);
    }

    public CompetitionAssessmentConfigResourceBuilder withInterviewStage(Boolean... hasInterviewStage) {
        return withArray((streamName, object) -> setField("hasInterviewStage", streamName, object), hasInterviewStage);
    }

    public CompetitionAssessmentConfigResourceBuilder withAssessorFinanceView(AssessorFinanceView... assessorFinanceView) {
        return withArray((streamName, object) -> setField("assessorFinanceView", streamName, object), assessorFinanceView);
    }
}
