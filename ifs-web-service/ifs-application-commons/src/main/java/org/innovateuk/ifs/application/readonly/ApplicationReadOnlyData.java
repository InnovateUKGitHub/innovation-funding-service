package org.innovateuk.ifs.application.readonly;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class ApplicationReadOnlyData {

    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final UserResource user;
    private final Optional<ProcessRoleResource> processRole;

    private final Map<Long, QuestionResource> questionIdToQuestion;
    private final Multimap<Long, FormInputResource> questionIdToFormInputs;
    private final Map<Long, FormInputResponseResource> formInputIdToFormInputResponses;
    private final Multimap<Long, QuestionStatusResource> questionToQuestionStatus;


    public ApplicationReadOnlyData(ApplicationResource application, CompetitionResource competition, UserResource user, Optional<ProcessRoleResource> processRole, List<QuestionResource> questions, List<FormInputResource> formInputs, List<FormInputResponseResource> formInputResponses, List<QuestionStatusResource> questionStatuses) {
        this.application = application;
        this.competition = competition;
        this.user = user;
        this.processRole = processRole;

        this.questionIdToQuestion = questions.stream()
                .collect(toMap(QuestionResource::getId, Function.identity()));
        this.questionIdToFormInputs = Multimaps.index(formInputs, FormInputResource::getQuestion);
        this.formInputIdToFormInputResponses = formInputResponses.stream()
                .collect(toMap(FormInputResponseResource::getFormInput, Function.identity(), (m1, m2) -> m1));
        this.questionToQuestionStatus = Multimaps.index(questionStatuses, QuestionStatusResource::getQuestion);
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

    public Multimap<Long, QuestionStatusResource> getQuestionToQuestionStatus() {
        return questionToQuestionStatus;
    }

    public Optional<ProcessRoleResource> getProcessRole() {
        return processRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationReadOnlyData that = (ApplicationReadOnlyData) o;

        return new EqualsBuilder()
                .append(competition, that.competition)
                .append(application, that.application)
                .append(user, that.user)
                .append(processRole, that.processRole)
                .append(questionIdToQuestion, that.questionIdToQuestion)
                .append(questionIdToFormInputs, that.questionIdToFormInputs)
                .append(formInputIdToFormInputResponses, that.formInputIdToFormInputResponses)
                .append(questionToQuestionStatus, that.questionToQuestionStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competition)
                .append(application)
                .append(user)
                .append(processRole)
                .append(questionIdToQuestion)
                .append(questionIdToFormInputs)
                .append(formInputIdToFormInputResponses)
                .append(questionToQuestionStatus)
                .toHashCode();
    }
}

