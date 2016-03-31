package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

import java.util.List;
import java.util.Set;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionService {

    @NotSecured("TODO")
    ServiceResult<Question> getQuestionById(final Long id);

    @NotSecured("TODO")
    ServiceResult<Void> markAsComplete(final Long questionId,
                        final Long applicationId,
                        final Long markedAsCompleteById);


    @NotSecured("TODO")
    ServiceResult<Void> markAsInComplete(final Long questionId,
                          final Long applicationId,
                          final Long markedAsInCompleteById);

    @NotSecured("TODO")
    ServiceResult<Void> assign(final Long questionId,
                final Long applicationId,
                final Long assigneeId,
                final Long assignedById);

    @NotSecured("TODO")
    ServiceResult<Set<Long>> getMarkedAsComplete(Long applicationId,
                                  Long organisationId);

    @NotSecured("TODO")
    ServiceResult<Void> updateNotification(final Long questionStatusId,
                            final Boolean notify);

    @NotSecured("TODO")
    ServiceResult<List<Question>> findByCompetition(final Long competitionId);

    @NotSecured("TODO")
    ServiceResult<Question> getNextQuestion(final Long questionId);

    @NotSecured("TODO")
    ServiceResult<Question> getPreviousQuestionBySection(final Long sectionId);

    @NotSecured("TODO")
    ServiceResult<Question> getNextQuestionBySection(final Long sectionId);

    @NotSecured("TODO")
    ServiceResult<Question> getPreviousQuestion(final Long questionId);

    @NotSecured("TODO")
    ServiceResult<Boolean> isMarkedAsComplete(Question question, Long applicationId, Long organisationId);

    @NotSecured("TODO")
    ServiceResult<Question> getQuestionByFormInputType(String formInputTypeTitle);

    @NotSecured("TODO DW - implement when permissions available")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeId(Long questionId, Long applicationId);

    @NotSecured("TODO DW - implement when permissions available")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);

    @NotSecured("TODO DW - implement when permissions available")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(Long[] questionIds, Long applicationId, Long organisationId);

    @NotSecured("TODO DW - implement when permissions available")
    ServiceResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(Long applicationId, Long organisationId);

    @NotSecured("TODO DW - implement when permissions available")
    ServiceResult<QuestionStatusResource> getQuestionStatusResourceById(Long id);

    @NotSecured("TODO")
    ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(Long applicationId, Long assigneeId);
}
