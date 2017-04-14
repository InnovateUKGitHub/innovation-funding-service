package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
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

    public SectionResourceBuilder withQuestions(List<Long>... questions) {
        return withArraySetFieldByReflection("questions", questions);
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

    public SectionResourceBuilder withAssessorGuidanceDescription(String... assessorGuidanceDescriptions) {
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

    public SectionResourceBuilder withParentSection(Long... parentSections) {
        return withArray((parentSection, object) -> setField("parentSection", parentSection, object), parentSections);
    }

    public SectionResourceBuilder withChildSections(List<Long>... childSectionss) {
        return withArray((childSections, object) -> setField("childSections", childSections, object), childSectionss);
    }

    public SectionResourceBuilder withDisplayInAssessmentApplicationSummary(Boolean... displayInAssessmentApplicationSummaries) {
        return withArray((displayInAssessmentApplicationSummary, object) -> setField("displayInAssessmentApplicationSummary", displayInAssessmentApplicationSummary, object), displayInAssessmentApplicationSummaries);
    }

    public SectionResourceBuilder withType(SectionType... types) {
        return this.withArray((type, object) -> setField("type", type, object), types);
    }

    @Override
    protected SectionResource createInitial() {
        return new SectionResource();
    }
}
