package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionService {

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.application.resource.QuestionResource', 'READ')")
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


    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getMarkedAsComplete(Long applicationId,
                                  Long organisationId);

    @PreAuthorize("hasPermission(#questionStatusId, 'org.innovateuk.ifs.application.resource.QuestionStatusResource', 'UPDATE')")
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

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> isMarkedAsComplete(Question question, Long applicationId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getQuestionResourceByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<Question> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);
    
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

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(Long applicationId, Long assigneeId);
    
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionResource>> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update a question")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<QuestionResource> save(QuestionResource questionResource);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    @PostAuthorize("hasPermission(returnObject, 'ASSESS')")
    ServiceResult<QuestionResource> getQuestionByIdAndAssessmentId(Long questionId, Long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<QuestionResource>> getQuestionsByAssessmentId(Long assessmentId);
}
