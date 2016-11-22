package com.worth.ifs.application.builder;

import com.worth.ifs.*;
import com.worth.ifs.competition.resource.*;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class GuidanceRowResourceBuilder extends BaseBuilder<GuidanceRowResource, GuidanceRowResourceBuilder> {

    private GuidanceRowResourceBuilder(List<BiConsumer<Integer, GuidanceRowResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected GuidanceRowResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GuidanceRowResource>> actions) {
        return new GuidanceRowResourceBuilder(actions);
    }

    public static GuidanceRowResourceBuilder newFormInputGuidanceRowResourceBuilder() {
        return new GuidanceRowResourceBuilder(emptyList())
                .with(uniqueIds());
    }


    public GuidanceRowResourceBuilder withId(Long id) {
        return with(guidanceRow -> setField("id", id, guidanceRow));
    }


    public GuidanceRowResourceBuilder withFormInput(Long formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public GuidanceRowResourceBuilder withSubject(String subject) {
        return with(guidanceRow -> setField("subject", subject, guidanceRow));
    }

    public GuidanceRowResourceBuilder withJustification(String justification) {
        return with(guidanceRow -> setField("justification", justification, guidanceRow));
    }

    @Override
    protected GuidanceRowResource createInitial() {
        return new GuidanceRowResource();
    }
}
