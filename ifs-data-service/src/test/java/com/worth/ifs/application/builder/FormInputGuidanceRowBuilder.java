package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.form.domain.FormInput;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class FormInputGuidanceRowBuilder extends BaseBuilder<FormInputGuidanceRow, FormInputGuidanceRowBuilder> {

    private FormInputGuidanceRowBuilder(List<BiConsumer<Integer, FormInputGuidanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputGuidanceRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputGuidanceRow>> actions) {
        return new FormInputGuidanceRowBuilder(actions);
    }

    public static FormInputGuidanceRowBuilder newFormInputGuidanceRow() {
        return new FormInputGuidanceRowBuilder(emptyList())
                .with(uniqueIds());
    }

    public FormInputGuidanceRowBuilder withFormInput(FormInput formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public FormInputGuidanceRowBuilder withSubject(String subject) {
        return with(guidanceRow -> setField("subject", subject, guidanceRow));
    }

    public FormInputGuidanceRowBuilder withJustification(String justification) {
        return with(guidanceRow -> setField("justification", justification, guidanceRow));
    }

    @Override
    protected FormInputGuidanceRow createInitial() {
        return new FormInputGuidanceRow();
    }
}
