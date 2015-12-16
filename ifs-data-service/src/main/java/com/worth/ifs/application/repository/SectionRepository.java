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
    Section findByName(String name);
    Section findFirstByCompetitionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(Long competitionId, Integer priority);
    Section findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(Long competitionId, Long parentSectionId, Integer priority);
    Section findFirstByCompetitionIdAndPriorityLessThanAndQuestionGroupOrderByPriorityDesc(Long competitionId, Integer priority, boolean questionGroup);
    Section findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullAndQuestionGroupOrderByPriorityDesc(Long competitionId, Integer priority, boolean questionGroup);
    Section findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(Long competitionId, Long parentSectionId, Integer priority);
    Section findByQuestionsId(Long questionId);
}