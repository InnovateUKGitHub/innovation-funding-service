package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantSectionResource extends AbstractApplicantResource {

    private SectionResource section;

    private List<ApplicantQuestionResource> questions = new ArrayList<>();

    private ApplicantSectionResource parentSection;

    private List<ApplicantSectionResource> childSections = new ArrayList<>();

    public List<ApplicantQuestionResource> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ApplicantQuestionResource> questions) {
        this.questions = questions;
    }

    public SectionResource getSection() {
        return section;
    }

    public void setSection(SectionResource section) {
        this.section = section;
    }

    public ApplicantSectionResource getParentSection() {
        return parentSection;
    }

    public void setParentSection(ApplicantSectionResource parentSection) {
        this.parentSection = parentSection;
    }

    public void addChildSection(ApplicantSectionResource childSection) {
        childSections.add(childSection);
    }

    public void addQuestion(ApplicantQuestionResource question) {
        questions.add(question);
    }

    public List<ApplicantSectionResource> getChildSections() {
        return childSections;
    }

    public void setChildSections(List<ApplicantSectionResource> childSections) {
        this.childSections = childSections;
    }

    public List<ApplicantQuestionResource> questionsWithType(QuestionType questionType) {
        return allQuestions().filter(questionResource -> questionResource.getQuestion().getType().equals(questionType))
                .collect(Collectors.toList());
    }

    public Stream<ApplicantQuestionResource> allQuestions() {
        return Stream.concat(childSections.stream().map(ApplicantSectionResource::getQuestions).flatMap(List::stream),
                questions.stream());
    }

    public Stream<ApplicantQuestionStatusResource> allQuestionStatuses() {
        return allQuestions().map(ApplicantQuestionResource::getQuestionStatuses).flatMap(List::stream);
    }

    public Stream<ApplicantFormInputResponseResource> allResponses() {
        return allQuestions().map(ApplicantQuestionResource::getFormInputs)
                .flatMap(List::stream)
                .map(ApplicantFormInputResource::getResponse);
    }

    public Stream<ApplicantSectionResource> allSections() {
        return Stream.concat(childSections.stream(), Stream.of(this));
    }

}
