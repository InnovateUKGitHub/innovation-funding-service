package org.innovateuk.ifs.application.readonly;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;

public class ApplicationReadOnlyData implements BaseAnalyticsViewModel {

    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final UserResource user;
    private final List<ProcessRoleResource> applicationProcessRoles;
    private final Optional<ProcessRoleResource> usersProcessRole;

    private final Map<Long, QuestionResource> questionIdToQuestion;
    private final Multimap<Long, FormInputResource> questionIdToApplicationFormInputs;
    private final Map<Long, FormInputResource> formInputIdToAssessorFormInput;
    private final Map<Long, List<FormInputResponseResource>> formInputIdToFormInputResponses;
    /* only included if ApplicationReadOnlySettings for isIncludeStatuses is set. */
    private final Multimap<Long, QuestionStatusResource> questionToQuestionStatus;
    /* only included if ApplicationReadOnlySettings.includeAssessment is set. */
    private final Map<Long, ApplicationAssessmentResource> assessmentToApplicationAssessment;
    /* only included if ApplicationReadOnlySettings.includeAllSupporterFeedback is set. */
    private final Map<Long, SupporterAssignmentResource> feedbackToApplicationSupport;

    public ApplicationReadOnlyData(ApplicationResource application, CompetitionResource competition,
                                   UserResource user, List<ProcessRoleResource> processRoles,
                                   List<QuestionResource> questions, List<FormInputResource> formInputs,
                                   List<FormInputResponseResource> formInputResponses,
                                   List<QuestionStatusResource> questionStatuses,
                                   List<ApplicationAssessmentResource> assessments,
                                   List<SupporterAssignmentResource> assignments) {
        this.application = application;
        this.competition = competition;
        this.user = user;
        this.applicationProcessRoles = processRoles;
        this.usersProcessRole = processRoles.stream().
                filter(pr -> pr.getUser().equals(user.getId()))
                .findAny();

        this.questionIdToQuestion = questions.stream()
                .collect(toMap(QuestionResource::getId, Function.identity()));
        this.questionIdToApplicationFormInputs = Multimaps.index(formInputs.stream()
                        .filter(input -> APPLICATION.equals(input.getScope())).collect(Collectors.toSet()),
                FormInputResource::getQuestion);
        this.formInputIdToAssessorFormInput = formInputs.stream()
                .filter(input -> ASSESSMENT.equals(input.getScope()))
                .collect(toMap(FormInputResource::getId, Function.identity()));
        this.formInputIdToFormInputResponses = formInputResponses.stream()
                .collect(Collectors.groupingBy(FormInputResponseResource::getFormInput));
        this.questionToQuestionStatus = Multimaps.index(questionStatuses, QuestionStatusResource::getQuestion);
        this.assessmentToApplicationAssessment = assessments.stream()
                .collect(toMap(ApplicationAssessmentResource::getAssessmentId, Function.identity()));
        this.feedbackToApplicationSupport = assignments.stream()
                .collect(toMap(SupporterAssignmentResource::getAssignmentId, Function.identity()));
    }

    public BigDecimal getApplicationScore() {
        BigDecimal totalAssessmentScore = assessmentToApplicationAssessment.values()
                .stream()
                .map(v -> v.getAveragePercentage())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (assessmentToApplicationAssessment.size() == 0) return BigDecimal.ZERO;

        BigDecimal average = totalAssessmentScore.divide(BigDecimal.valueOf(assessmentToApplicationAssessment.size()), 1, BigDecimal.ROUND_HALF_UP);

        return average;
    }

    @Override
    public Long getApplicationId() {
        return application.getId();
    }

    @Override
    public String getCompetitionName() {
        return competition.getName();
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

    public Multimap<Long, FormInputResource> getQuestionIdToApplicationFormInputs() {
        return questionIdToApplicationFormInputs;
    }

    public Map<Long, List<FormInputResponseResource>> getFormInputIdToFormInputResponses() {
        return formInputIdToFormInputResponses;
    }

    public Map<Long, FormInputResource> getFormInputIdToAssessorFormInput() {
        return formInputIdToAssessorFormInput;
    }

    public Multimap<Long, QuestionStatusResource> getQuestionToQuestionStatus() {
        return questionToQuestionStatus;
    }

    public List<ProcessRoleResource> getApplicationProcessRoles() {
        return applicationProcessRoles;
    }

    public Optional<ProcessRoleResource> getUsersProcessRole() {
        return usersProcessRole;
    }

    public Map<Long, ApplicationAssessmentResource> getAssessmentToApplicationAssessment() {
        return assessmentToApplicationAssessment;
    }

    public Map<Long, SupporterAssignmentResource> getFeedbackToApplicationSupport() {
        return feedbackToApplicationSupport;
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
                .append(applicationProcessRoles, that.applicationProcessRoles)
                .append(usersProcessRole, that.usersProcessRole)
                .append(questionIdToQuestion, that.questionIdToQuestion)
                .append(questionIdToApplicationFormInputs, that.questionIdToApplicationFormInputs)
                .append(formInputIdToFormInputResponses, that.formInputIdToFormInputResponses)
                .append(questionToQuestionStatus, that.questionToQuestionStatus)
                .append(assessmentToApplicationAssessment, that.assessmentToApplicationAssessment)
                .append(feedbackToApplicationSupport, that.feedbackToApplicationSupport)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competition)
                .append(application)
                .append(user)
                .append(applicationProcessRoles)
                .append(usersProcessRole)
                .append(questionIdToQuestion)
                .append(questionIdToApplicationFormInputs)
                .append(formInputIdToFormInputResponses)
                .append(questionToQuestionStatus)
                .append(assessmentToApplicationAssessment)
                .append(feedbackToApplicationSupport)
                .toHashCode();
    }
}

