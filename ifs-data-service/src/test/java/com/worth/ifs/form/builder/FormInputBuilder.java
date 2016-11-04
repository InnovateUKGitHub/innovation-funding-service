package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.domain.FormInputType;

import java.util.List;
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

    public FormInputBuilder withQuestion(Question... questions) {
        return withArray((question, formInput) -> setField("question", question, formInput), questions);
    }

    public FormInputBuilder withPriority(Integer... priorities) {
        return withArray((priority, formInput) -> setField("priority", priority, formInput), priorities);
    }

    public FormInputBuilder withScope(FormInputScope... scopes) {
        return withArray((scope, formInput) -> setField("scope", scope, formInput), scopes);
    }
}
