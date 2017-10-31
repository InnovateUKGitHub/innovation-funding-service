package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.Question;
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
	Question findFirstByIdAndSectionName(Long questionId, String sectionName);
    List<Question> findByCompetitionId(Long competitionId);
    Question findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, Long sectionId, Integer priority);
    Question findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(Long competitionId, Long sectionId, Integer priority);
    Question findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(Long competitionId, Long sectionId);
    List<Question> findByCompetitionIdAndSectionNameOrderByPriorityAsc(Long competitionId, String sectionName);
    Question findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(Long competitionId, String sectionName);
    Question findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(Long competitionId, Long sectionId);
    List<Question> findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, String sectionName, Integer priority);
    Question findFirstByCompetitionIdAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, Integer priority);
    long countByCompetitionIdAndSectionName(Long competitionId, String sectionName);
}
