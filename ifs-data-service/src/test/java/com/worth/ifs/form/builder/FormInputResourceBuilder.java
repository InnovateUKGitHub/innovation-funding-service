package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
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

    public static FormInputResourceBuilder newFormInputResource() {
        return new FormInputResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedDescriptions("Description "));
    }

    public FormInputResourceBuilder withId(Long... ids) {
        return withArray((id, formInput) -> formInput.setId(id), ids);
    }

    public FormInputResourceBuilder withWordCount(Integer... wordCount) {
        return withArray((id, formInput) -> setField("wordCount", id, formInput), wordCount);
    }

    public FormInputResourceBuilder withFormInputType(Long formInputType) {
        return with(formInput -> formInput.setFormInputType(formInputType));
    }

    public FormInputResourceBuilder withFormInputTypeTitle(String formInputTypeTitle) {
        return with(formInput -> formInput.setFormInputTypeTitle(formInputTypeTitle));
    }

    @Override
    protected FormInputResource createInitial() {
        return new FormInputResource();
    }
}
