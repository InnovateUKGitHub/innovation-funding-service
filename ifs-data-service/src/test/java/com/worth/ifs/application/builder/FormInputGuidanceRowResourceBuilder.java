package com.worth.ifs.application.builder;

import com.worth.ifs.*;
import com.worth.ifs.application.resource.*;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class FormInputGuidanceRowResourceBuilder extends BaseBuilder<FormInputGuidanceRowResource, FormInputGuidanceRowResourceBuilder> {

    private FormInputGuidanceRowResourceBuilder(List<BiConsumer<Integer, FormInputGuidanceRowResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FormInputGuidanceRowResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputGuidanceRowResource>> actions) {
        return new FormInputGuidanceRowResourceBuilder(actions);
    }

    public static FormInputGuidanceRowResourceBuilder newFormInputGuidanceRowResourceBuilder() {
        return new FormInputGuidanceRowResourceBuilder(emptyList())
                .with(uniqueIds());
    }


    public FormInputGuidanceRowResourceBuilder withId(Long id) {
        return with(guidanceRow -> setField("id", id, guidanceRow));
    }


    public FormInputGuidanceRowResourceBuilder withFormInput(Long formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public FormInputGuidanceRowResourceBuilder withSubject(String subject) {
        return with(guidanceRow -> setField("subject", subject, guidanceRow));
    }

    public FormInputGuidanceRowResourceBuilder withJustification(String justification) {
        return with(guidanceRow -> setField("justification", justification, guidanceRow));
    }

    @Override
    protected FormInputGuidanceRowResource createInitial() {
        return new FormInputGuidanceRowResource();
    }
}
