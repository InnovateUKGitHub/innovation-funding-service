package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.QuestionResource;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantQuestionResource extends AbstractApplicantResource {

    private QuestionResource question;

    private List<ApplicantFormInputResource> formInputs;

    private List<ApplicantQuestionStatusResource> questionStatuses;

    public QuestionResource getQuestion() {
        return question;
    }

    public void setQuestion(QuestionResource question) {
        this.question = question;
    }

    public List<ApplicantFormInputResource> getFormInputs() {
        return formInputs;
    }

    public void setFormInputs(List<ApplicantFormInputResource> formInputs) {
        this.formInputs = formInputs;
    }

    public List<ApplicantQuestionStatusResource> getQuestionStatuses() {
        return questionStatuses;
    }

    public void setQuestionStatuses(List<ApplicantQuestionStatusResource> questionStatuses) {
        this.questionStatuses = questionStatuses;
    }

    public Stream<ApplicantFormInputResponseResource> allResponses() {
        return getFormInputs().stream()
                .map(ApplicantFormInputResource::getApplicantResponses)
                .flatMap(List::stream);
    }

    public Stream<ApplicantQuestionStatusResource> allCompleteStatuses() {
        return getQuestionStatuses().stream().filter(status -> status.getMarkedAsCompleteBy() != null);
    }

    public Stream<ApplicantQuestionStatusResource> allAssignedStatuses() {
        return getQuestionStatuses().stream().filter(status -> status.getAssignee() != null);
    }
}
