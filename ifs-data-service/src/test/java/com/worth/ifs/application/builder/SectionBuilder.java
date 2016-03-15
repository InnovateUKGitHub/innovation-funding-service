package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
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
                .with(idBasedNames("Question "))
                .withDisplayInAssessmentApplicationSummary(true);
    }

    public SectionBuilder withQuestions(List<Question> questions) {
        return with(section -> section.setQuestions(questions));
    }

    @Override
    public List<Section> build(int numberToBuild) {

        // build the sections, and then apply any back refs if necessary
        List<Section> sections = super.build(numberToBuild);

        sections.forEach(section -> section.getQuestions().forEach(question -> question.setSection(section)));

        sections.forEach(section -> getCompetition(section).ifPresent(competition -> {

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
        }));

        return sections;
    }

    public SectionBuilder withQuestionSets(List<List<Question>> questionSets) {
        return withList(questionSets, (questions, section) -> section.setQuestions(questions));
    }

    public SectionBuilder withDisplayInAssessmentApplicationSummary(boolean displayInSummary) {
        return with(section -> setField("displayInAssessmentApplicationSummary", displayInSummary, section));
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
