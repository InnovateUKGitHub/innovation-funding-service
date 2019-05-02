package org.innovateuk.ifs.application.summary;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class ApplicationSummaryData {

    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final UserResource user;

    private final Map<Long, QuestionResource> questionIdToQuestion;
    private final Multimap<Long, FormInputResource> questionIdToFormInputs;
    private final Map<Long, FormInputResponseResource> formInputIdToFormInputResponses;
    private final Map<Long, QuestionStatusResource> questionToQuestionStatus;


    public ApplicationSummaryData(ApplicationResource application, CompetitionResource competition, UserResource user, List<QuestionResource> questions, List<FormInputResource> formInputs, List<FormInputResponseResource> formInputResponses, List<QuestionStatusResource> questionStatuses) {
        this.application = application;
        this.competition = competition;
        this.user = user;

        this.questionIdToQuestion = questions.stream()
                .collect(toMap(QuestionResource::getId, Function.identity()));
        this.questionIdToFormInputs = Multimaps.index(formInputs, FormInputResource::getQuestion);
        this.formInputIdToFormInputResponses = formInputResponses.stream()
                .collect(toMap(FormInputResponseResource::getFormInput, Function.identity()));
        this.questionToQuestionStatus = questionStatuses.stream()
                .filter(status -> status.getMarkedAsCompleteBy() != null)
                .collect(toMap(QuestionStatusResource::getQuestion, Function.identity()));
    }

    public Map<Long, QuestionResource> getQuestionIdToQuestion() {
        return questionIdToQuestion;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public UserResource getUser() {
        return user;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public Multimap<Long, FormInputResource> getQuestionIdToFormInputs() {
        return questionIdToFormInputs;
    }

    public Map<Long, FormInputResponseResource> getFormInputIdToFormInputResponses() {
        return formInputIdToFormInputResponses;
    }

    public Map<Long, QuestionStatusResource> getQuestionToQuestionStatus() {
        return questionToQuestionStatus;
    }
}

