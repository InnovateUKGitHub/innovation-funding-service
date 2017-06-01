package org.innovateuk.ifs.applicant.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.resource.QuestionResource;

import java.util.List;
import java.util.stream.Stream;

/**
 * Rich resource for an application question.
 */
public class ApplicantQuestionResource extends AbstractApplicantResource {

    private QuestionResource question;

    private List<ApplicantFormInputResource> applicantFormInputs;

    private List<ApplicantQuestionStatusResource> applicantQuestionStatuses;

    public QuestionResource getQuestion() {
        return question;
    }

    public void setQuestion(QuestionResource question) {
        this.question = question;
    }

    public List<ApplicantFormInputResource> getApplicantFormInputs() {
        return applicantFormInputs;
    }

    public void setApplicantFormInputs(List<ApplicantFormInputResource> applicantFormInputs) {
        this.applicantFormInputs = applicantFormInputs;
    }

    public List<ApplicantQuestionStatusResource> getApplicantQuestionStatuses() {
        return applicantQuestionStatuses;
    }

    public void setApplicantQuestionStatuses(List<ApplicantQuestionStatusResource> applicantQuestionStatuses) {
        this.applicantQuestionStatuses = applicantQuestionStatuses;
    }

    public Stream<ApplicantFormInputResponseResource> allResponses() {
        return getApplicantFormInputs().stream()
                .map(ApplicantFormInputResource::getApplicantResponses)
                .flatMap(List::stream);
    }

    public Stream<ApplicantQuestionStatusResource> allCompleteStatuses() {
        return getApplicantQuestionStatuses().stream().filter(status -> status.getMarkedAsCompleteBy() != null);
    }

    @JsonIgnore
    public boolean isCompleteByApplicant(ApplicantResource applicantResource) {
        return getApplicantQuestionStatuses().stream().filter(status ->
                Boolean.TRUE.equals(status.getStatus().getMarkedAsComplete()) &&
                (!this.getQuestion().getMultipleStatuses() ||
                        (status.getMarkedAsCompleteBy() != null && status.getMarkedAsCompleteBy().hasSameOrganisation(applicantResource)))).count() > 0;
    }

    public Stream<ApplicantQuestionStatusResource> allAssignedStatuses() {
        return getApplicantQuestionStatuses().stream().filter(status -> status.getAssignee() != null);
    }
}
