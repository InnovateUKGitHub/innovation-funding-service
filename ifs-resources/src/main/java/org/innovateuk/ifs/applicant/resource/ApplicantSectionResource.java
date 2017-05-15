package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Rich resource for a section of the application.
 */
public class ApplicantSectionResource extends AbstractApplicantResource {

    private SectionResource section;

    private List<ApplicantQuestionResource> applicantQuestions = new ArrayList<>();

    private ApplicantSectionResource applicantParentSection;

    private List<ApplicantSectionResource> applicantChildrenSections = new ArrayList<>();

    public List<ApplicantQuestionResource> getApplicantQuestions() {
        return applicantQuestions;
    }

    public void setApplicantQuestions(List<ApplicantQuestionResource> applicantQuestions) {
        this.applicantQuestions = applicantQuestions;
    }

    public SectionResource getSection() {
        return section;
    }

    public void setSection(SectionResource section) {
        this.section = section;
    }

    public ApplicantSectionResource getApplicantParentSection() {
        return applicantParentSection;
    }

    public void setApplicantParentSection(ApplicantSectionResource applicantParentSection) {
        this.applicantParentSection = applicantParentSection;
    }

    public void addChildSection(ApplicantSectionResource childSection) {
        applicantChildrenSections.add(childSection);
    }

    public void addQuestion(ApplicantQuestionResource question) {
        applicantQuestions.add(question);
    }

    public List<ApplicantSectionResource> getApplicantChildrenSections() {
        return applicantChildrenSections;
    }

    public void setApplicantChildrenSections(List<ApplicantSectionResource> applicantChildrenSections) {
        this.applicantChildrenSections = applicantChildrenSections;
    }

    public List<ApplicantQuestionResource> questionsWithType(QuestionType questionType) {
        return allQuestions().filter(questionResource -> questionResource.getQuestion().getType().equals(questionType))
                .collect(Collectors.toList());
    }

    public Stream<ApplicantQuestionResource> allQuestions() {
        return Stream.concat(applicantChildrenSections.stream().map(ApplicantSectionResource::getApplicantQuestions).flatMap(List::stream),
                applicantQuestions.stream());
    }

    public Stream<ApplicantQuestionStatusResource> allCompleteQuestionStatuses() {
        return allQuestions().flatMap(ApplicantQuestionResource::allCompleteStatuses);
    }
    public Stream<ApplicantQuestionStatusResource> allAssignedQuestionStatuses() {
        return allQuestions().flatMap(ApplicantQuestionResource::allAssignedStatuses);
    }

    public Stream<ApplicantFormInputResponseResource> allResponses() {
        return allQuestions().flatMap(ApplicantQuestionResource::allResponses);
    }

    public Stream<ApplicantSectionResource> allSections() {
        return Stream.concat(applicantChildrenSections.stream(), Stream.of(this));
    }

    public boolean isComplete(ApplicantResource applicant) {
        return allQuestions().allMatch(questionResource -> questionResource.isCompleteByApplicant(applicant));
    }

}
