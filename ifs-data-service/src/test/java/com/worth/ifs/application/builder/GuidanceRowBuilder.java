package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.form.domain.FormInput;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class GuidanceRowBuilder extends BaseBuilder<GuidanceRow, GuidanceRowBuilder> {

    private GuidanceRowBuilder(List<BiConsumer<Integer, GuidanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected GuidanceRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GuidanceRow>> actions) {
        return new GuidanceRowBuilder(actions);
    }

    public static GuidanceRowBuilder newFormInputGuidanceRow() {
        return new GuidanceRowBuilder(emptyList())
                .with(uniqueIds());
    }

    public GuidanceRowBuilder withFormInput(FormInput formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public GuidanceRowBuilder withSubject(String subject) {
        return with(guidanceRow -> setField("subject", subject, guidanceRow));
    }

    public GuidanceRowBuilder withJustification(String justification) {
        return with(guidanceRow -> setField("justification", justification, guidanceRow));
    }

    @Override
    protected GuidanceRow createInitial() {
        return new GuidanceRow();
    }
}
