package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {
	@Override
    List<Question> findAll();
	List<Question> findAllById(long [] id);
	Question findFirstById(Long questionId);
    List<Question> findByCompetitionId(Long competitionId);
    Question findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, Long sectionId, Integer priority);
    Question findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(Long competitionId, Long sectionId, Integer priority);
    Question findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(Long competitionId, Long sectionId);
    List<Question> findByCompetitionIdAndSectionTypeOrderByPriorityAsc(Long competitionId, SectionType sectionType);
    Question findFirstByCompetitionIdAndSectionTypeOrderByPriorityDesc(Long competitionId, SectionType sectionType);
    Question findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(Long competitionId, Long sectionId);
    List<Question> findByCompetitionIdAndSectionTypeAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, SectionType sectionType, Integer priority);
    Question findFirstByCompetitionIdAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, Integer priority);
    Question findFirstByCompetitionIdAndQuestionSetupType(long competitionId, QuestionSetupType
            questionSetupType);
    long countByCompetitionId(Long competitionId);
    default long countQuestionsWithMultipleStatuses(long competitionId) {
        return countByCompetitionIdAndMultipleStatusesAndMarkAsCompletedEnabledTrue(competitionId, true);
    }
    default long countQuestionsWithSingleStatus(long competitionId) {
        return countByCompetitionIdAndMultipleStatusesAndMarkAsCompletedEnabledTrue(competitionId, false);
    }
    long countByCompetitionIdAndMultipleStatusesAndMarkAsCompletedEnabledTrue(long competitionId, boolean multipleStatuses);

    Question findByQuestionnaireId(long id);
}
