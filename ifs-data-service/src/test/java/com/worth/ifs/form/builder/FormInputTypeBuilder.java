package com.worth.ifs.form.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.form.domain.FormInputType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedTitles;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FormInputTypeBuilder extends BaseBuilder<FormInputType, FormInputTypeBuilder> {

    private FormInputTypeBuilder(List<BiConsumer<Integer, FormInputType>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputTypeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputType>> actions) {
        return new FormInputTypeBuilder(actions);
    }

    @Override
    protected FormInputType createInitial() {
        return new FormInputType();
    }

    public static FormInputTypeBuilder newFormInputType() {
        return new FormInputTypeBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedTitles("Title "));
    }

    public FormInputTypeBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public FormInputTypeBuilder withTitle(String... value) {
        return withArraySetFieldByReflection("title", value);
    }
}