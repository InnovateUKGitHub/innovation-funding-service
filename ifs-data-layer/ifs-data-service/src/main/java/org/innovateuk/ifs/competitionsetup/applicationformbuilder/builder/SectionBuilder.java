package org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder;

import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SectionBuilder {
    private String name;
    private String description;
    private String assessorGuidanceDescription;
    private boolean questionGroup;
    private List<QuestionBuilder> questions = new ArrayList<>();
    private List<SectionBuilder> childSections = new ArrayList<>();
    private boolean displayInAssessmentApplicationSummary = false;
    private SectionType type = SectionType.GENERAL;

    private SectionBuilder() {
    }

    public static SectionBuilder aSection() {
        SectionBuilder sb = new SectionBuilder();
        sb.questionGroup = false;
        sb.displayInAssessmentApplicationSummary = true;
        return sb;
    }

    public static SectionBuilder aSubSection() {
        SectionBuilder sb = new SectionBuilder();
        sb.questionGroup = true;
        sb.displayInAssessmentApplicationSummary = false;
        return sb;
    }

    public SectionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SectionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SectionBuilder withAssessorGuidanceDescription(String assessorGuidanceDescription) {
        this.assessorGuidanceDescription = assessorGuidanceDescription;
        return this;
    }

    public SectionBuilder withQuestionGroup(boolean questionGroup) {
        this.questionGroup = questionGroup;
        return this;
    }

    public SectionBuilder withQuestions(List<QuestionBuilder> questions) {
        this.questions = questions;
        return this;
    }

    public SectionBuilder withChildSections(List<SectionBuilder> childSections) {
        this.childSections = childSections;
        return this;
    }

    public SectionBuilder withDisplayInAssessmentApplicationSummary(boolean displayInAssessmentApplicationSummary) {
        this.displayInAssessmentApplicationSummary = displayInAssessmentApplicationSummary;
        return this;
    }

    public SectionBuilder withType(SectionType type) {
        this.type = type;
        return this;
    }

    public SectionBuilder addChildSection(SectionBuilder childSection) {
        this.childSections.add(childSection);
        return this;
    }

    public List<QuestionBuilder> getQuestions() {
        return questions;
    }

    public SectionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAssessorGuidanceDescription() {
        return assessorGuidanceDescription;
    }

    public List<SectionBuilder> getChildSections() {
        return childSections;
    }

    public Section build() {
        Section section = new Section();
        section.setName(name);
        section.setAssessorGuidanceDescription(assessorGuidanceDescription);
        section.setQuestionGroup(questionGroup);
        section.setQuestions(questions.stream().map(QuestionBuilder::build).collect(Collectors.toList()));
        section.setChildSections(childSections.stream().map(SectionBuilder::build).collect(Collectors.toList()));
        section.setDisplayInAssessmentApplicationSummary(displayInAssessmentApplicationSummary);
        section.setType(type);
        return section;
    }
}
