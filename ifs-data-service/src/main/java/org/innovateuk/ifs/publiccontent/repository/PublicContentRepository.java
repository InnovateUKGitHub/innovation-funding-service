package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface PublicContentRepository extends PagingAndSortingRepository<PublicContent, Long> {
    @Query("SELECT p FROM PublicContent p " +
                "INNER JOIN p.competition c " +
                "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > :now AND closed_milestone.type='SUBMISSION_DATE') " +
                "INNER JOIN c.milestones open_milestone ON (open_milestone.date < :now AND open_milestone.type='OPEN_DATE') " +
            "WHERE p.competition.id IN :competitionIds " +
                "AND p.publishDate < :now " +
            "ORDER BY closed_milestone.date ASC")
    Page<PublicContent> findAllPublishedForOpenCompetitionByInnovationId(@Param(value="competitionIds") List<Long> competitionIds, Pageable pageable, @Param(value="now") LocalDateTime now);

    @Query("SELECT p FROM PublicContent p " +
                "INNER JOIN p.competition c " +
                "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > :now AND closed_milestone.type='SUBMISSION_DATE') " +
                "INNER JOIN c.milestones open_milestone ON (open_milestone.date < :now AND open_milestone.type='OPEN_DATE') " +
            "WHERE p.id IN :filteredPublicContentIds " +
                "AND p.publishDate < :now " +
            "ORDER BY closed_milestone.date ASC")
    Page<PublicContent> findAllPublishedForOpenCompetitionBySearchString(@Param(value="filteredPublicContentIds") Set<Long> filteredPublicContentIds, Pageable pageable, @Param(value="now") LocalDateTime now);

    @Query("SELECT p FROM PublicContent p " +
                "INNER JOIN p.competition c " +
                "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > :now AND closed_milestone.type='SUBMISSION_DATE') " +
                "INNER JOIN c.milestones open_milestone ON (open_milestone.date < :now AND open_milestone.type='OPEN_DATE') " +
            "WHERE p.competition.id IN :competitionIds " +
                "AND p.id IN :filteredPublicContentIds " +
                "AND p.publishDate < :now " +
            "ORDER BY closed_milestone.date ASC")
    Page<PublicContent> findAllPublishedForOpenCompetitionByKeywordsAndInnovationId(@Param(value="filteredPublicContentIds") Set<Long> filteredPublicContentIds, @Param(value="competitionIds") List<Long> competitionIds, Pageable pageable, @Param(value="now") LocalDateTime now);

    @Query("SELECT p FROM PublicContent p " +
                "INNER JOIN p.competition c " +
                "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > :now AND closed_milestone.type='SUBMISSION_DATE') " +
                "INNER JOIN c.milestones open_milestone ON (open_milestone.date < :now AND open_milestone.type='OPEN_DATE') " +
            "WHERE p.publishDate < :now " +
            "ORDER BY closed_milestone.date ASC")
    Page<PublicContent> findAllPublishedForOpenCompetition(Pageable pageable, @Param(value="now") LocalDateTime now);

    PublicContent findByCompetitionId(Long id);
    Page<PublicContent> findByCompetitionIdInAndIdIn(List<Long> competitionIds, Set<Long> publicContentIds, Pageable pageable);
    Page<PublicContent> findByCompetitionIdIn(List<Long> competitionIds, Pageable pageable);
    Page<PublicContent> findByIdIn(Set<Long> publicContentIds, Pageable pageable);
}
