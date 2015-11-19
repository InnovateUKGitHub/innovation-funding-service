package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.form.domain.FormInputResponse;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * A Builder for Form Inputs.  By default this builder will assign unique ids and descriptions based upon the ids.
 * It will also assign priorities.
 */
public class FormInputResponseBuilder extends BaseBuilder<FormInputResponse, FormInputResponseBuilder> {

    private FormInputResponseBuilder(List<BiConsumer<Integer, FormInputResponse>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputResponseBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResponse>> actions) {
        return new FormInputResponseBuilder(actions);
    }

    public static FormInputResponseBuilder newFormInputResponse() {
        return new FormInputResponseBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedValues("Value "));
    }

    @Override
    protected FormInputResponse createInitial() {
        return new FormInputResponse();
    }
}
