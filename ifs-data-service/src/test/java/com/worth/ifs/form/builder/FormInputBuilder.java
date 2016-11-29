package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.GuidanceRow;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.resource.FormInputType;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputBuilder extends BaseBuilder<FormInput, FormInputBuilder> {

    private FormInputBuilder(List<BiConsumer<Integer, FormInput>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInput>> actions) {
        return new FormInputBuilder(actions);
    }

    @Override
    protected FormInput createInitial() {
        return new FormInput();
    }

    public static FormInputBuilder newFormInput() {
        return new FormInputBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedDescriptions("Description "));
    }

    public FormInputBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public FormInputBuilder withWordCount(Integer... wordCounts) {
        return withArray((wordCount, formInput) -> setField("wordCount", wordCount, formInput), wordCounts);
    }

    public FormInputBuilder withActive(Boolean... active) {
        return withArraySetFieldByReflection("active", active);
    }

    public FormInputBuilder withCompetition(Competition... value) {
        return withArraySetFieldByReflection("competition", value);
    }

    public FormInputBuilder withType(FormInputType... value) {
        return withArraySetFieldByReflection("type", value);
    }

    public FormInputBuilder withQuestion(Question... questions) {
        return withArray((question, formInput) -> setField("question", question, formInput), questions);
    }

    public FormInputBuilder withPriority(Integer... priorities) {
        return withArray((priority, formInput) -> setField("priority", priority, formInput), priorities);
    }

    public FormInputBuilder withResponses(List<FormInputResponse>... value) {
        return withArraySetFieldByReflection("responses", value);
    }

    public FormInputBuilder withScope(FormInputScope... scopes) {
        return withArray((scope, formInput) -> setField("scope", scope, formInput), scopes);
    }

    public FormInputBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public FormInputBuilder withGuidanceAnswer(String... value) {
        return withArraySetFieldByReflection("guidanceAnswer", value);
    }

    public FormInputBuilder withGuidanceQuestion(String... value) {
        return withArraySetFieldByReflection("guidanceQuestion", value);
    }

    public FormInputBuilder withIncludedInApplicationSummary(Boolean... value) {
        return withArraySetFieldByReflection("includedInApplicationSummary", value);
    }

    public FormInputBuilder withInputValidators(Set<FormValidator>... inputValidators) {
        return withArraySetFieldByReflection("inputValidators", inputValidators);
    }

    public FormInputBuilder withGuidanceRows(List<GuidanceRow>... value) {
        return withArraySetFieldByReflection("guidanceRows", value);
    }
}
