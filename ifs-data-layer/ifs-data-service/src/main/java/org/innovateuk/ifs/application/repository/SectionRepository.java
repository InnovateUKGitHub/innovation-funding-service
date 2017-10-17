package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.Section;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {
    @Override
    List<Section> findAll();

    List<Section> findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(Long competitionId);

    Section findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(Long competitionId, Integer priority);

    Section findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(Long competitionId, Long parentSectionId, Integer priority);

    Section findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(Long competitionId, Integer priority);

    Section findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(Long competitionId, Long parentSectionId, Integer priority);

    Section findByQuestionsId(Long questionId);

    Section findFirstByCompetitionIdAndName(Long competitionId, String sectionName);

    List<Section> findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(Long competitionId);
}
