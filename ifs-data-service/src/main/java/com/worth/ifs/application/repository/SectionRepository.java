package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Section;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {
    List<Section> findAll();
    Section findByName(@Param("name") String name);
    Section findFirstByCompetitionIdAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, Integer priority);
    Section findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanOrderByPriorityAsc(Long competitionId, Long parentSectionId, Integer priority);
    Section findFirstByCompetitionIdAndPriorityLessThanOrderByPriorityDesc(Long competitionId, Integer priority);
    Section findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanOrderByPriorityDesc(Long competitionId, Long parentSectionId, Integer priority);
    Section findByQuestionsId(Long questionId);
}