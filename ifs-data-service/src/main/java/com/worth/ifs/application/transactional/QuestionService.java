package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionService {

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.application.resource.QuestionResource', 'READ')")
    ServiceResult<QuestionResource> getQuestionById(final Long id);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<List<ValidationMessages>> markAsComplete(final QuestionApplicationCompositeId ids,
                                                            final Long markedAsCompleteById);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<List<ValidationMessages>> markAsInComplete(final QuestionApplicationCompositeId ids,
                          final Long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<Void> assign(final QuestionApplicationCompositeId ids,
                final Long assigneeId,
                final Long assignedById);


    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getMarkedAsComplete(Long applicationId,
                                  Long organisationId);

    @PreAuthorize("hasPermission(#questionStatusId, 'com.worth.ifs.application.resource.QuestionStatusResource', 'UPDATE')")
    ServiceResult<Void> updateNotification(final Long questionStatusId,
                            final Boolean notify);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionResource>> findByCompetition(final Long competitionId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getNextQuestion(final Long questionId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getPreviousQuestionBySection(final Long sectionId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getNextQuestionBySection(final Long sectionId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getPreviousQuestion(final Long questionId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> isMarkedAsComplete(Question question, Long applicationId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getQuestionResourceByFormInputType(String formInputTypeTitle);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<Question> getQuestionByFormInputType(String formInputTypeTitle);
    
    @PostFilter("hasPermission(filterObject, 'READ')")
	ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(Long questionId, Long applicationId);

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
    
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionResource>> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<QuestionResource> save(QuestionResource questionResource);

    @PreAuthorize("hasPermission(#assessmentId, 'com.worth.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<QuestionResource>> getQuestionsByAssessmentId(final Long assessmentId);
}
