package org.innovateuk.ifs.questionnaire.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.questionnaire.resource.DecisionType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class QuestionnaireOptionResourceBuilder extends BaseBuilder<QuestionnaireOptionResource, QuestionnaireOptionResourceBuilder> {
    private QuestionnaireOptionResourceBuilder(List<BiConsumer<Integer, QuestionnaireOptionResource>> multiActions) {
        super(multiActions);
    }

    public static QuestionnaireOptionResourceBuilder newQuestionnaireOptionResource() {
        return new QuestionnaireOptionResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected QuestionnaireOptionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionnaireOptionResource>> actions) {
        return new QuestionnaireOptionResourceBuilder(actions);
    }

    @Override
    protected QuestionnaireOptionResource createInitial() {
        return new QuestionnaireOptionResource();
    }

    public QuestionnaireOptionResourceBuilder withId(Long... ids) {
        return withArray((id, resource) -> resource.setId(id), ids);
    }

    public QuestionnaireOptionResourceBuilder withText(String... values) {
        return withArray((value, resource) -> resource.setText(value), values);
    }

    public QuestionnaireOptionResourceBuilder withDecision(Long... values) {
        return withArray((value, resource) -> resource.setDecision(value), values);
    }

    public QuestionnaireOptionResourceBuilder withQuestion(Long... values) {
        return withArray((value, resource) -> resource.setQuestion(value), values);
    }

    public QuestionnaireOptionResourceBuilder withDecisionType(DecisionType... values) {
        return withArray((value, resource) -> resource.setDecisionType(value), values);
    }
}