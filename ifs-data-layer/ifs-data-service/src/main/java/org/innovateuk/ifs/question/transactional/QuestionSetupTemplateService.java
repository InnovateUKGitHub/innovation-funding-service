package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Question;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service interface defining security rules for creating adding or deleting questions.
 */
public interface QuestionSetupTemplateService {
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "ADD_QUESTION_TO_COMPETITION",
            description = "The Competition Admin user and Project Finance users should be able to create competition assessed questions")
    ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "DELETE_QUESTION_FROM_COMPETITION",
            description = "The Competition Admin user and Project Finance users should be able to delete competition questions")
    ServiceResult<Void> deleteQuestionInCompetition(long questionId);
}
