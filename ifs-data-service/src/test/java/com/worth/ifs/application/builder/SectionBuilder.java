package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.competition.domain.Competition;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.getCompetition;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;
import static org.springframework.test.util.ReflectionTestUtils.getField;

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
                .with(idBasedNames("Section "));
    }

    public SectionBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    @Override
    public void postProcess(int index, Section section) {

        section.getQuestions().forEach(question -> question.setSection(section));

        getCompetition(section).ifPresent(competition -> {

            List<Section> existingCompetitionSections = competition.getSections();

            if (!existingCompetitionSections.contains(section)) {
                existingCompetitionSections.add(section);
            }

            List<Question> competitionQuestions = (List<Question>) getField(competition, "questions");
            List<Question> newQuestions = new ArrayList<>();

            if (competitionQuestions != null) {
                newQuestions.addAll(competitionQuestions);
            }

            section.getQuestions().forEach(question -> {

                if (!newQuestions.contains(question)) {
                    newQuestions.add(question);
                }
            });

            newQuestions.addAll(section.getQuestions());
            setField("questions", newQuestions, competition);
        });
    }

    public SectionBuilder withQuestions(List<Question>... questions) {
        return withArray((questionSet, object) -> setField("questions", questionSet, object), questions);
    }

    public SectionBuilder withDisplayInAssessmentApplicationSummary(Boolean... displayInAssessmentApplicationSummaries) {
        return withArray((displayInAssessmentApplicationSummary, object) -> setField("displayInAssessmentApplicationSummary", displayInAssessmentApplicationSummary, object), displayInAssessmentApplicationSummaries);
    }
    
    public SectionBuilder withChildSections(List<Section> childSections) {
    	return with(section -> setField("childSections", childSections, section));
    }

    public SectionBuilder withName(String name) {
        return with(section -> setField("name", name, section));
    }

    public SectionBuilder withSectionType(SectionType sectionType) {
        return with(section -> setField("type", sectionType, section));
    }

    public SectionBuilder withDescription(String description) {
        return with(section -> setField("description", description, section));
    }

    public SectionBuilder withParentSection(Section parentSection) {
        return with(section -> setField("parentSection", parentSection, section));
    }

    public SectionBuilder withAssessorGuidanceDescription(String assessorGuidanceDescription) {
        return with(section -> setField("assessorGuidanceDescription", assessorGuidanceDescription, section));
    }

    public SectionBuilder withCompetitionAndPriority(Competition competition, Integer priority) {
        return with(section -> {
            section.setCompetition(competition);
            setField("priority", priority, section);
        });
    }

    public SectionBuilder withCompetitionAndPriorityAndParent(Competition competition, Integer priority, Section parentSection) {
        return with(section -> {
            section.setParentSection(parentSection);
            section.setCompetition(competition);
            setField("priority", priority, section);
        });
    }


    @Override
    protected Section createInitial() {
        return new Section();
    }
}
