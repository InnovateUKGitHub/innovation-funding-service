package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Created by dwatson on 03/11/15.
 */
public class SectionBuilder extends BaseBuilder<Section, SectionBuilder> {

    private SectionBuilder(List<BiConsumer<Integer, Section>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected SectionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Section>> actions) {
        return new SectionBuilder(actions);
    }

    public static SectionBuilder newSection() {
        return new SectionBuilder(emptyList());
    }

    public static SectionBuilder withQuestions() {
        return new SectionBuilder(emptyList());
    }

    @Override
    protected Section createInitial() {
        return new Section();
    }
}
