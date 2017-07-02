package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
public interface CompetitionInviteRepository extends PagingAndSortingRepository<CompetitionInvite, Long> {

    CompetitionInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<CompetitionInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<CompetitionInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    CompetitionInvite getByHash(String hash);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);


    String USERS_WITH_COMPETITION_INVITE = "SELECT invite.user.id " +
            "FROM CompetitionInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    @Query("SELECT user " +
            "FROM User user " +
            "JOIN user.roles roles " +
            "WHERE user.id NOT IN (" + USERS_WITH_COMPETITION_INVITE + ") " +
            "AND roles.name = 'assessor' " +
            "GROUP BY user.id")
    Page<User> findAssessorsByCompetition(@Param("competitionId") long competitionId, Pageable pageable);

    /**
     * We have to explicitly join {@link User} and Profile due to the relational mapping
     * on {@link User} being removed. This join was not possible without using a
     * cartesian product before Hibernate 5.1 (nearly 12 years to implement).
     * <p>
     * Unfortunately, due to this explicit join, we cannot leverage Spring JPAs {@link Specification}
     * as we have to use the {@link Query} annotation to create this query.
     * <p>
     * We should try to be clever with how we write the query. Otherwise, we will have to have
     * multiple repository methods that have different parameters (and this will get out of hand quickly).
     * <p>
     * Try to keep any other required filtering parameters in this query.
     */
    @Query("SELECT user " +
            "FROM User user " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            "JOIN profile.innovationAreas innovationAreas " +
            "JOIN user.roles roles " +
            "WHERE (innovationAreas.category.id = :innovationArea OR :innovationArea IS NULL) " +
            "AND user.id NOT IN (" + USERS_WITH_COMPETITION_INVITE + ") " +
            "AND roles.name = 'assessor' " +
            "GROUP BY user.id")
    Page<User> findAssessorsByCompetitionAndInnovationArea(@Param("competitionId") long competitionId,
                                                           @Param("innovationArea") Long innovationArea,
                                                           Pageable pageable);
}
