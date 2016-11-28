package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputResourceBuilder extends BaseBuilder<FormInputResource, FormInputResourceBuilder> {

    private FormInputResourceBuilder(List<BiConsumer<Integer, FormInputResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResource>> actions) {
        return new FormInputResourceBuilder(actions);
    }

    @Override
    protected FormInputResource createInitial() {
        return new FormInputResource();
    }

    public static FormInputResourceBuilder newFormInputResource() {
        return new FormInputResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedDescriptions("Description "));
    }

    public FormInputResourceBuilder withId(Long... ids) {
        return withArray((id, formInput) -> formInput.setId(id), ids);
    }

    public FormInputResourceBuilder withWordCount(Integer... wordCounts) {
        return withArray((wordCount, formInput) -> setField("wordCount", wordCount, formInput), wordCounts);
    }

    public FormInputResourceBuilder withFormInputType(Long formInputType) {
        return with(formInput -> formInput.setFormInputType(formInputType));
    }

    public FormInputResourceBuilder withFormInputTypeTitle(String... formInputTypeTitles) {
        return withArray((formInputTypeTitle, formInput) -> setField("formInputTypeTitle", formInputTypeTitle, formInput), formInputTypeTitles);
    }

    public FormInputResourceBuilder withQuestion(Long... questions) {
        return withArray((question, formInput) -> setField("question", question, formInput), questions);
    }

    public FormInputResourceBuilder withPriority(Integer... priorities) {
        return withArray((priority, formInput) -> setField("priority", priority, formInput), priorities);
    }

    public FormInputResourceBuilder withScope(FormInputScope... scopes) {
        return withArray((scope, formInput) -> setField("scope", scope, formInput), scopes);
    }
}
