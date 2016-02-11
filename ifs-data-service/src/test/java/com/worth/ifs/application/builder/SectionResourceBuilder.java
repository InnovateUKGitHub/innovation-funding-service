package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.SectionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class SectionResourceBuilder extends BaseBuilder<SectionResource, SectionResourceBuilder> {

    private SectionResourceBuilder(List<BiConsumer<Integer, SectionResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected SectionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SectionResource>> actions) {
        return new SectionResourceBuilder(actions);
    }

    public static SectionResourceBuilder newSectionResource() {
        return new SectionResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Question "))
                .withDisplayInAssessmentApplicationSummary(true);
    }

    public SectionResourceBuilder withQuestions(List<Long> questions) {
        return with(section -> section.setQuestions(questions));
    }

    @Override
    public List<SectionResource> build(int numberToBuild) {

        // build the sections, and then apply any back refs if necessary
        List<SectionResource> sections = super.build(numberToBuild);

        return sections;
    }

    public SectionResourceBuilder withQuestionSets(List<List<Long>> questionSets) {
        return withList(questionSets, (questions, section) -> section.setQuestions(questions));
    }

    public SectionResourceBuilder withDisplayInAssessmentApplicationSummary(boolean displayInSummary) {
        return with(section -> setField("displayInAssessmentApplicationSummary", displayInSummary, section));
    }

    public SectionResourceBuilder withCompetitionAndPriority(Long competition, Integer priority) {
        return with(section -> {
            section.setCompetition(competition);
            setField("priority", priority, section);
        });
    }

    public SectionResourceBuilder withCompetitionAndPriorityAndParent(Long competition, Integer priority, Long parentSection) {
        return with(section -> {
            section.setParentSection(parentSection);
            section.setCompetition(competition);
            setField("priority", priority, section);
        });
    }

    @Override
    protected SectionResource createInitial() {
        return new SectionResource();
    }
}
