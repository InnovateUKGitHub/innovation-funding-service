package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.form.domain.FormInput;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.worth.ifs.BuilderAmendFunctions.*;
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

    public static FormInputBuilder newFormInput() {
        return new FormInputBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedDescriptions("Description "));
    }

    @Override
    protected FormInput createInitial() {
        return new FormInput();
    }
}
