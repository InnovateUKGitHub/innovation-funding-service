package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
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
        return new SectionBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Question "))
                .withDisplayInAssessmentApplicationSummary(true);
    }

    public SectionBuilder withQuestions(List<Question> questions) {
        return with(section -> section.setQuestions(questions));
    }

    public SectionBuilder withQuestionSets(List<List<Question>> questionSets) {
        return withList((questions, section) -> section.setQuestions(questions), questionSets);
    }

    public SectionBuilder withDisplayInAssessmentApplicationSummary(boolean display) {
        return with(section -> setField("displayInAssessmentApplicationSummary", display, section));
    }

    @Override
    protected Section createInitial() {
        return new Section();
    }
}
