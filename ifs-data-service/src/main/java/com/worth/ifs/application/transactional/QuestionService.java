package com.worth.ifs.application.transactional;

import java.util.List;
import java.util.Set;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionService {

    @NotSecured("Any loggedIn user can get a question")
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


    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getMarkedAsComplete(Long applicationId,
                                  Long organisationId);

    @NotSecured("TODO")
    ServiceResult<Void> updateNotification(final Long questionStatusId,
                            final Boolean notify);

    @NotSecured("Any loggedIn user can get a question")
    ServiceResult<List<Question>> findByCompetition(final Long competitionId);

    @NotSecured("Any loggedIn user can get a question")
    ServiceResult<Question> getNextQuestion(final Long questionId);

    @NotSecured("Any loggedIn user can get a question")
    ServiceResult<Question> getPreviousQuestionBySection(final Long sectionId);

    @NotSecured("Any loggedIn user can get a question")
    ServiceResult<Question> getNextQuestionBySection(final Long sectionId);

    @NotSecured("Any loggedIn user can get a question")
    ServiceResult<Question> getPreviousQuestion(final Long questionId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> isMarkedAsComplete(Question question, Long applicationId, Long organisationId);

    @NotSecured("Any loggedIn user can get a question")
    ServiceResult<Question> getQuestionByFormInputType(String formInputTypeTitle);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeId(Long questionId, Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(Long[] questionIds, Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(Long applicationId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionStatusResource> getQuestionStatusResourceById(Long id);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(Long applicationId, Long assigneeId);
}
