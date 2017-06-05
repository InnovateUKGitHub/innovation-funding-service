package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface PublicContentRepository extends PagingAndSortingRepository<PublicContent, Long> {
    @Query("SELECT c FROM Competition c " +
            "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > CURRENT_TIMESTAMP AND closed_milestone.type='SUBMISSION_DATE') " +
            "WHERE EXISTS(   SELECT p " +
                            "FROM PublicContent p " +
                            "WHERE p.competitionId = c.id " +
                            "AND p.publishDate < CURRENT_TIMESTAMP " +
                            "AND p.inviteOnly = false)" +
            "AND c.id IN :competitionIds " +
            "ORDER BY closed_milestone.date ASC")
    Page<Competition> findAllPublishedForOpenCompetitionByInnovationId(@Param(value="competitionIds") List<Long> competitionIds, Pageable pageable);

    @Query("SELECT c FROM Competition c " +
            "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > CURRENT_TIMESTAMP AND closed_milestone.type='SUBMISSION_DATE') " +
            "WHERE EXISTS(   SELECT p " +
                            "FROM PublicContent p " +
                            "WHERE p.competitionId = c.id " +
                                "AND p.id IN :filteredPublicContentIds " +
                                "AND p.publishDate < CURRENT_TIMESTAMP " +
                                "AND p.inviteOnly = false)" +
            "ORDER BY closed_milestone.date ASC")
    Page<Competition> findAllPublishedForOpenCompetitionByKeywords(@Param(value="filteredPublicContentIds") Set<Long> filteredPublicContentIds, Pageable pageable);
    
    @Query("SELECT c FROM Competition c " +
            "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > CURRENT_TIMESTAMP AND closed_milestone.type='SUBMISSION_DATE') " +
            "WHERE EXISTS(   SELECT p " +
                            "FROM PublicContent p " +
                            "WHERE p.competitionId = c.id " +
                                "AND p.id IN :filteredPublicContentIds " +
                                "AND p.publishDate < CURRENT_TIMESTAMP " +
                                "AND p.inviteOnly = false)" +
            "AND c.id IN :competitionIds " +
            "ORDER BY closed_milestone.date ASC")

    Page<Competition> findAllPublishedForOpenCompetitionByKeywordsAndInnovationId(@Param(value="filteredPublicContentIds") Set<Long> filteredPublicContentIds, @Param(value="competitionIds") List<Long> competitionIds, Pageable pageable);
    @Query("SELECT c FROM Competition c " +
            "INNER JOIN c.milestones closed_milestone ON (closed_milestone.date > CURRENT_TIMESTAMP AND closed_milestone.type='SUBMISSION_DATE') " +
            "WHERE EXISTS(   SELECT p " +
                            "FROM PublicContent p " +
                            "WHERE p.competitionId = c.id " +
                            "AND p.publishDate < CURRENT_TIMESTAMP " +
                            "AND p.inviteOnly = false)" +
            "ORDER BY closed_milestone.date ASC")
    Page<Competition> findAllPublishedForOpenCompetition(Pageable pageable);

    PublicContent findByCompetitionId(Long id);
    Page<PublicContent> findByCompetitionIdInAndIdIn(List<Long> competitionIds, Set<Long> publicContentIds, Pageable pageable);
    Page<PublicContent> findByCompetitionIdIn(List<Long> competitionIds, Pageable pageable);
    Page<PublicContent> findByIdIn(Set<Long> publicContentIds, Pageable pageable);
}
