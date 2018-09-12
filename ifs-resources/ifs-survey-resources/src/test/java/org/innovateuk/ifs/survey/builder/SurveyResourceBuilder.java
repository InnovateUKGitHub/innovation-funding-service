package org.innovateuk.ifs.survey.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.survey.Satisfaction;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.SurveyTargetType;
import org.innovateuk.ifs.survey.SurveyType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class SurveyResourceBuilder extends BaseBuilder<SurveyResource, SurveyResourceBuilder> {

    private SurveyResourceBuilder(List<BiConsumer<Integer, SurveyResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static SurveyResourceBuilder newSurveyResource() {
        return new SurveyResourceBuilder(emptyList());
    }

    @Override
    protected SurveyResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SurveyResource>> actions) {
        return new SurveyResourceBuilder(actions);
    }

    @Override
    protected SurveyResource createInitial() {
        return new SurveyResource();
    }

    public SurveyResourceBuilder withSurveyType(SurveyType... surveyTypes) {
        return withArraySetFieldByReflection("surveyType", surveyTypes);
    }

    public SurveyResourceBuilder withSurveyTargetType(SurveyTargetType... targetTypes) {
        return withArraySetFieldByReflection("targetType", targetTypes);
    }

    public SurveyResourceBuilder withSatisfaction(Satisfaction... satisfactions) {
        return withArraySetFieldByReflection("satisfaction", satisfactions);
    }

    public SurveyResourceBuilder withTargetId(Long... targetIds) {
        return withArraySetFieldByReflection("targetId", targetIds);
    }

    public SurveyResourceBuilder withComments(String... comments) {
        return withArraySetFieldByReflection("comments", comments);
    }
}
