package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.GuidanceRowResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionSetupQuestionResourceBuilder extends BaseBuilder<CompetitionSetupQuestionResource, CompetitionSetupQuestionResourceBuilder> {

    private CompetitionSetupQuestionResourceBuilder(List<BiConsumer<Integer, CompetitionSetupQuestionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionSetupQuestionResourceBuilder newCompetitionSetupQuestionResource() {
        return new CompetitionSetupQuestionResourceBuilder(emptyList());
    }

    public CompetitionSetupQuestionResourceBuilder withQuestionId(Long questionId) {
        return with(competition -> competition.setQuestionId(questionId));
    }

    public CompetitionSetupQuestionResourceBuilder withNumber(String number) {
        return with(competition -> competition.setNumber(number));
    }

    public CompetitionSetupQuestionResourceBuilder withShortTitle(String shortTitle) {
        return with(competition -> competition.setShortTitle(shortTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withTitle(String title) {
        return with(competition -> competition.setTitle(title));
    }

    public CompetitionSetupQuestionResourceBuilder withSubTitle(String subTitle) {
        return with(competition -> competition.setSubTitle(subTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withGuidanceTitle(String guidanceTitle) {
        return with(competition -> competition.setGuidanceTitle(guidanceTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withGuidance(String guidance) {
        return with(competition -> competition.setGuidance(guidance));
    }

    public CompetitionSetupQuestionResourceBuilder withMaxWords(Integer maxWords) {
        return with(competition -> competition.setMaxWords(maxWords));
    }

    public CompetitionSetupQuestionResourceBuilder withAppendix(Boolean appendix) {
        return with(competition -> competition.setAppendix(appendix));
    }

    public CompetitionSetupQuestionResourceBuilder withAssessmentGuidance(String assessmentGuidance) {
        return with(competition -> competition.setAssessmentGuidance(assessmentGuidance));
    }

    public CompetitionSetupQuestionResourceBuilder withAssessmentMaxWords(Integer assessmentMaxWords) {
        return with(competition -> competition.setAssessmentMaxWords(assessmentMaxWords));
    }

    public CompetitionSetupQuestionResourceBuilder withScored(Boolean scored) {
        return with(competition -> competition.setScored(scored));
    }

    public CompetitionSetupQuestionResourceBuilder withScoreTotal(Integer scoreTotal) {
        return with(competition -> competition.setScoreTotal(scoreTotal));
    }

    public CompetitionSetupQuestionResourceBuilder withWrittenFeedback(Boolean writtenFeedback) {
        return with(competition -> competition.setWrittenFeedback(writtenFeedback));
    }

    public CompetitionSetupQuestionResourceBuilder withGuidanceRows(List<GuidanceRowResource> guidanceRows) {
        return with(competition -> competition.setGuidanceRows(guidanceRows));
    }

    @Override
    protected CompetitionSetupQuestionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionSetupQuestionResource>> actions) {
        return new CompetitionSetupQuestionResourceBuilder(actions);
    }

    @Override
    protected CompetitionSetupQuestionResource createInitial() {
        return new CompetitionSetupQuestionResource();
    }
}
