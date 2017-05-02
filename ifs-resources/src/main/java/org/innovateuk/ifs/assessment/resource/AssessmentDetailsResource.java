package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Objects.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 */
public class AssessmentDetailsResource {
    private List<QuestionResource> questions;
    private Map<Long, List<FormInputResource>> assessmentFormInputs;
    private Map<Long, List<AssessorFormInputResponseResource>> assessorFormInputResponses;

    public void setQuestions(List<QuestionResource> questions) {
        this.questions = questions;
    }

    public Map<Long, List<FormInputResource>> getAssessmentFormInputs() {
        return assessmentFormInputs;
    }

    public void setAssessmentFormInputs(Map<Long, List<FormInputResource>> assessmentFormInputs) {
        this.assessmentFormInputs = assessmentFormInputs;
    }

    public Map<Long, List<AssessorFormInputResponseResource>> getAssessorFormInputResponses() {
        return assessorFormInputResponses;
    }

    public void setAssessorFormInputResponses(Map<Long, List<AssessorFormInputResponseResource>> assessorFormInputResponses) {
        this.assessorFormInputResponses = assessorFormInputResponses;
    }

    public AssessmentDetailsResource() {
    }

    @Deprecated
    public AssessmentDetailsResource(List<QuestionResource> questions,
                                     Map<Long, List<FormInputResource>> assessmentFormInputs,
                                     Map<Long, List<AssessorFormInputResponseResource>> assessorFormInputResponses) {
        requireNonNull(questions, "questions cannot be null");
        requireNonNull(assessmentFormInputs, "assessmentFormInputs cannot be null");
        requireNonNull(assessorFormInputResponses, "assessorFormInputResponses cannot be null");

        this.questions = questions;
        this.assessmentFormInputs = assessmentFormInputs;
        this.assessorFormInputResponses = assessorFormInputResponses;
    }

    public AssessmentDetailsResource(List<QuestionResource> questions,
                                     List<FormInputResource> assessmentFormInputs,
                                     List<AssessorFormInputResponseResource> assessorResponses) {
        this(questions, assessmentFormInputs.stream().collect(groupingBy(FormInputResource::getQuestion)), assessorResponses.stream().collect(groupingBy(AssessorFormInputResponseResource::getQuestion)));
    }

    public List<QuestionResource> getQuestions() {
        return questions;
    }

    public List<FormInputResource> getFormInputsForQuestion(long questionId) {
        requireNonNull(assessmentFormInputs, "assessmentFormInputs cannot be null xx");

        return ofNullable(assessmentFormInputs.get(questionId)).orElse(emptyList());
    }

    public List<AssessorFormInputResponseResource> getFormInputResponsesForQuestion(long questionId) {
        return ofNullable(assessorFormInputResponses.get(questionId)).orElse(emptyList());
    }
}