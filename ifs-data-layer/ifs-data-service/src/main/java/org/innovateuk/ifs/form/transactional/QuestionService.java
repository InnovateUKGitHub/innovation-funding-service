package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionService {

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.form.resource.QuestionResource', 'READ')")
    ServiceResult<QuestionResource> getQuestionById(final Long id);

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

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getQuestionResourceByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<Question> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionResource> getQuestionByCompetitionIdAndQuestionSetupType(long competitionId,
                                                                                   QuestionSetupType questionSetupType);

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
