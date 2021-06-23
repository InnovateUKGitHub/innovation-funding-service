package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

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

    List<Section> findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(Long competitionId);

    Optional<Section> findByTypeAndCompetitionId(SectionType type, long competitionId);
}
