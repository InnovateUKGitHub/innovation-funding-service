package com.worth.ifs.application.builder;

import com.worth.ifs.*;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.competition.domain.*;
import com.worth.ifs.finance.domain.*;
import com.worth.ifs.form.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class QuestionBuilder extends BaseBuilder<Question, QuestionBuilder> {

    private QuestionBuilder(List<BiConsumer<Integer, Question>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Question>> actions) {
        return new QuestionBuilder(actions);
    }

    public static QuestionBuilder newQuestion() {
        return new QuestionBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Section "))
                .withPriority(0)
                .withQuestionNumber("1");
    }

    public QuestionBuilder withQuestionNumber(String value) {
        return with(question -> setField("questionNumber", value, question));
    }

    public QuestionBuilder withPriority(int priority) {
        return with(question -> setField("priority", priority, question));
    }
    
    public QuestionBuilder withQuestionType(QuestionType type) {
        return with(question -> setField("type", type, question));
    }

    public QuestionBuilder withPriority(Function<Integer, Integer> prioritySetter) {
        return with((i, question) -> setField("priority", prioritySetter.apply(i), question));
    }

    public QuestionBuilder withFormInputs(List<FormInput> formInputs) {
        return with(question -> setField("formInputs", new ArrayList<>(formInputs), question));
    }

    public QuestionBuilder withCompetitionAndSectionAndPriority(Competition competition, Section section, Integer priority) {
        return with(question -> {
            question.setCompetition(competition);
            question.setSection(section);
            setField("priority", priority, question);
        });
    }

    public QuestionBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }

    public QuestionBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public QuestionBuilder withShortName(String... shortNames) {
        return withArray((shortName, object) -> setField("shortName", shortName, object), shortNames);
    }

    public QuestionBuilder withDescription(String... descriptions) {
        return withArray((description, object) -> setField("description", description, object), descriptions);
    }

    public QuestionBuilder withCompetition(Competition... competitions) {
        return withArray((competition, object) -> setField("competition", competition, object), competitions);
    }

    public QuestionBuilder withSection(Section... sections) {
        return withArray((section, object) -> setField("section", section, object), sections);
    }

    public QuestionBuilder withMultipleStatuses(Boolean... multipleStatuses) {
        return withArray((multipleStatus, object) -> setField("multipleStatuses", multipleStatus, object), multipleStatuses);
    }

    public QuestionBuilder withQuestionStatuses(List<QuestionStatus>... questionStatuses) {
        return withArray((questionStatus, object) -> setField("questionStatuses", questionStatus, object), questionStatuses);
    }

    public QuestionBuilder withCosts(List<FinanceRow>... costs) {
        return withArray((cost, object) -> setField("costs", cost, object), costs);
    }

    public QuestionBuilder withAssessorMaximumScore(Integer... assessorMaximumScores) {
        return withArray((assessorMaximumScore, object) -> setField("assessorMaximumScore", assessorMaximumScore, object), assessorMaximumScores);
    }

    @Override
    protected Question createInitial() {
        return new Question();
    }
}
