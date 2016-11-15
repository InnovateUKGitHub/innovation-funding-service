package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.resource.SectionType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
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
                .with(idBasedNames("Section "))
                .withDisplayInAssessmentApplicationSummary(true);
    }

    public SectionResourceBuilder withQuestions(List<Long> questions) {
        return with(section -> section.setQuestions(questions));
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

    public SectionResourceBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public SectionResourceBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public SectionResourceBuilder withDescription(String... descriptions) {
        return withArray((description, object) -> setField("description", description, object), descriptions);
    }

    public SectionResourceBuilder withassessorGuidanceDescription(String... assessorGuidanceDescriptions) {
        return withArray((assessorGuidanceDescription, object) -> setField("assessorGuidanceDescription", assessorGuidanceDescription, object), assessorGuidanceDescriptions);
    }

    public SectionResourceBuilder withPriority(Integer... prioritys) {
        return withArray((priority, object) -> setField("priority", priority, object), prioritys);
    }

    public SectionResourceBuilder withQuestionGroup(Boolean... questionGroups) {
        return withArray((questionGroup, object) -> setField("questionGroup", questionGroup, object), questionGroups);
    }

    public SectionResourceBuilder withCompetition(Long... competitions) {
        return withArray((competition, object) -> setField("competition", competition, object), competitions);
    }

    public SectionResourceBuilder withQuestions(List<Long>... questionss) {
        return withArray((questions, object) -> setField("questions", questions, object), questionss);
    }

    public SectionResourceBuilder withParentSection(Long... parentSections) {
        return withArray((parentSection, object) -> setField("parentSection", parentSection, object), parentSections);
    }

    public SectionResourceBuilder withChildSections(List<Long>... childSectionss) {
        return withArray((childSections, object) -> setField("childSections", childSections, object), childSectionss);
    }

    public SectionResourceBuilder withType(SectionType... types) {
        return this.withArray((type, object) -> setField("type", type, object), types);
    }

    @Override
    protected SectionResource createInitial() {
        return new SectionResource();
    }
}
