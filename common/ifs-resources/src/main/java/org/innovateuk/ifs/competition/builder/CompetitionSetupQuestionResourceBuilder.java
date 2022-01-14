package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.file.resource.FileTypeCategory;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class CompetitionSetupQuestionResourceBuilder extends BaseBuilder<CompetitionSetupQuestionResource, CompetitionSetupQuestionResourceBuilder> {

    private CompetitionSetupQuestionResourceBuilder(List<BiConsumer<Integer, CompetitionSetupQuestionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionSetupQuestionResourceBuilder newCompetitionSetupQuestionResource() {
        return new CompetitionSetupQuestionResourceBuilder(emptyList());
    }

    public CompetitionSetupQuestionResourceBuilder withQuestionId(Long questionId) {
        return with(competition -> competition.setQuestionId(questionId));
    }

    public CompetitionSetupQuestionResourceBuilder withNumber(String number) {
        return with(competition -> competition.setNumber(number));
    }

    public CompetitionSetupQuestionResourceBuilder withShortTitle(String shortTitle) {
        return with(competition -> competition.setShortTitle(shortTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withTitle(String title) {
        return with(competition -> competition.setTitle(title));
    }

    public CompetitionSetupQuestionResourceBuilder withSubTitle(String subTitle) {
        return with(competition -> competition.setSubTitle(subTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withGuidanceTitle(String guidanceTitle) {
        return with(competition -> competition.setGuidanceTitle(guidanceTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withGuidance(String guidance) {
        return with(competition -> competition.setGuidance(guidance));
    }

    public CompetitionSetupQuestionResourceBuilder withMaxWords(Integer maxWords) {
        return with(competition -> competition.setMaxWords(maxWords));
    }

    public CompetitionSetupQuestionResourceBuilder withTextArea(Boolean textArea) {
        return with(competition -> competition.setTextArea(textArea));
    }

    public CompetitionSetupQuestionResourceBuilder withTemplateDocument(Boolean templateDocument) {
        return with(competition -> competition.setTemplateDocument(templateDocument));
    }

    public CompetitionSetupQuestionResourceBuilder withTemplateTitle(String templateTitle) {
        return with(competition -> competition.setTemplateTitle(templateTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withMultipleChoice(Boolean multipleChoice) {
        return with(competition -> competition.setMultipleChoice(multipleChoice));
    }

    public CompetitionSetupQuestionResourceBuilder withMultipleChoiceOptions(List<MultipleChoiceOptionResource> choices) {
        return with(competition -> competition.setChoices(choices));
    }

    public CompetitionSetupQuestionResourceBuilder withAppendix(Boolean appendix) {
        return with(competition -> competition.setAppendix(appendix));
    }

    public CompetitionSetupQuestionResourceBuilder withNumberOfUploads(Integer numberOfUploads) {
        return with(competition -> competition.setNumberOfUploads(numberOfUploads));
    }

    public CompetitionSetupQuestionResourceBuilder withAllowedFileTypes(Set<FileTypeCategory>... fileTypes) {
        return withArray((fileType, competition) -> setField("allowedFileTypes", fileType, competition), fileTypes);
    }

    public CompetitionSetupQuestionResourceBuilder withAppendixGuidance(String appendixGuidance) {
        return with(competition -> competition.setAppendixGuidance(appendixGuidance));
    }


    public CompetitionSetupQuestionResourceBuilder withAssessmentGuidanceTitle(String assessmentGuidanceTitle) {
        return with(competition -> competition.setAssessmentGuidanceTitle(assessmentGuidanceTitle));
    }

    public CompetitionSetupQuestionResourceBuilder withAssessmentGuidance(String assessmentGuidance) {
        return with(competition -> competition.setAssessmentGuidance(assessmentGuidance));
    }

    public CompetitionSetupQuestionResourceBuilder withAssessmentMaxWords(Integer assessmentMaxWords) {
        return with(competition -> competition.setAssessmentMaxWords(assessmentMaxWords));
    }

    public CompetitionSetupQuestionResourceBuilder withScored(Boolean scored) {
        return with(competition -> competition.setScored(scored));
    }

    public CompetitionSetupQuestionResourceBuilder withScoreTotal(Integer scoreTotal) {
        return with(competition -> competition.setScoreTotal(scoreTotal));
    }

    public CompetitionSetupQuestionResourceBuilder withWrittenFeedback(Boolean writtenFeedback) {
        return with(competition -> competition.setWrittenFeedback(writtenFeedback));
    }

    public CompetitionSetupQuestionResourceBuilder withGuidanceRows(List<GuidanceRowResource> guidanceRows) {
        return with(competition -> competition.setGuidanceRows(guidanceRows));
    }

    public CompetitionSetupQuestionResourceBuilder withType(QuestionSetupType types) {
        return with(competition -> competition.setType(types));
    }

    @Override
    protected CompetitionSetupQuestionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionSetupQuestionResource>> actions) {
        return new CompetitionSetupQuestionResourceBuilder(actions);
    }

    @Override
    protected CompetitionSetupQuestionResource createInitial() {
        return new CompetitionSetupQuestionResource();
    }
}
