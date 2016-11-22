package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.domain.GuidanceRow;
import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.domain.FormInputType;

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

    public FormInputBuilder withFormInputType(FormInputType formInputType) {
        return with(formInput -> formInput.setFormInputType(formInputType));
    }


    public FormInputBuilder withFormInputType(String title) {
        FormInputType type = new FormInputType();
        type.setTitle(title);
        return with(formInput -> formInput.setFormInputType(type));
    }

    public FormInputBuilder withQuestion(Question... questions) {
        return withArray((question, formInput) -> setField("question", question, formInput), questions);
    }

    public FormInputBuilder withPriority(Integer... priorities) {
        return withArray((priority, formInput) -> setField("priority", priority, formInput), priorities);
    }

    public FormInputBuilder withScope(FormInputScope... scopes) {
        return withArray((scope, formInput) -> setField("scope", scope, formInput), scopes);
    }

    public FormInputBuilder withDescription(String description) {
        return with(formInput -> formInput.setDescription(description));
    }

    public FormInputBuilder withGuidanceAnswer(String guidanceAnswer) {
        return with(formInput -> formInput.setGuidanceAnswer(guidanceAnswer));
    }

    public FormInputBuilder withGuidanceQuestion(String guidanceQuestion) {
        return with(formInput -> formInput.setGuidanceQuestion(guidanceQuestion));
    }

    public FormInputBuilder withIncludedInApplicationSummary(boolean includedInApplicationSummary) {
        return with(formInput -> formInput.setIncludedInApplicationSummary(includedInApplicationSummary));
    }

    public FormInputBuilder withInputValidators(Set<FormValidator> inputValidators) {
        return with(formInput -> formInput.setInputValidators(inputValidators));
    }

    public FormInputBuilder withFormInputGuidanceRows(List<GuidanceRow> guidanceRows) {
        return with(formInput -> formInput.setGuidanceRows(guidanceRows));
    }
}
