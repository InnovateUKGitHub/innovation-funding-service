package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.competition.resource.CompetitionResource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class QuestionResourceBuilder extends BaseBuilder<QuestionResource, QuestionResourceBuilder> {

    private QuestionResourceBuilder(List<BiConsumer<Integer, QuestionResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionResource>> actions) {
        return new QuestionResourceBuilder(actions);
    }

    public static QuestionResourceBuilder newQuestionResource() {
        return new QuestionResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Section "))
                .withPriority(0)
                .withQuestionNumber("1");
    }

    public QuestionResourceBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }

    public QuestionResourceBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public QuestionResourceBuilder withShortName(String... shortNames) {
        return withArray((shortName, object) -> setField("shortName", shortName, object), shortNames);
    }

    public QuestionResourceBuilder withDescription(String... descriptions) {
        return withArray((description, object) -> setField("description", description, object), descriptions);
    }

    public QuestionResourceBuilder withCompetition(Long... competitions) {
        return withArray((competition, object) -> setField("competition", competition, object), competitions);
    }

    public QuestionResourceBuilder withSection(Long... sections) {
        return withArray((section, object) -> setField("section", section, object), sections);
    }

    public QuestionResourceBuilder withQuestionNumber(String... questionNumbers) {
        return withArray((questionNumber, object) -> setField("questionNumber", questionNumber, object), questionNumbers);
    }

    public QuestionResourceBuilder withPriority(int priority) {
        return with(question -> setField("priority", priority, question));
    }

    public QuestionResourceBuilder withPriority(Function<Integer, Integer> prioritySetter) {
        return with((i, question) -> setField("priority", prioritySetter.apply(i), question));
    }

    public QuestionResourceBuilder withFormInputs(List<Long> formInputs) {
        return with(question -> setField("formInputs", new ArrayList<>(formInputs), question));
    }

    public QuestionResourceBuilder withAssessorMaximumScore(Integer... assessorMaximumScores) {
        return withArray((assessorMaximumScore, object) -> setField("assessorMaximumScore", assessorMaximumScore, object), assessorMaximumScores);
    }

    public QuestionResourceBuilder withCompetitionAndSectionAndPriority(CompetitionResource competition, SectionResource section, Integer priority) {
        return with(question -> {
            question.setCompetition(competition.getId());
            question.setSection(section.getId());
            setField("priority", priority, question);
        });
    }

    @Override
    protected QuestionResource createInitial() {
        return new QuestionResource();
    }
}
