package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") final UserStatus status);

    Optional<User> findByIdAndRolesName(Long id, String name);

    @Override
    List<User> findAll();

    List<User> findByRolesName(String name);

    Page<User> findDistinctByStatusAndRolesNameIn(UserStatus status, Set<String> roleName, Pageable pageable);

    User findOneByUid(String uid);

    String USERS_WITH_COMPETITION_INVITE = "SELECT invite.user.id " +
            "FROM CompetitionInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String ASSESSORS_WITH_COMPETITION = "SELECT user " +
            "FROM User user " +
            "JOIN user.roles roles " +
            "WHERE user.id NOT IN (" + USERS_WITH_COMPETITION_INVITE + ") " +
            "AND roles.name = 'assessor' "+
            "GROUP BY user.id ";

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
    String ASSESSORS_WITH_COMPETITION_AND_INNOVATION_AREA = "SELECT user " +
            "FROM User user " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            "JOIN profile.innovationAreas innovationAreas " +
            "JOIN user.roles roles " +
            "WHERE (innovationAreas.category.id = :innovationArea OR :innovationArea IS NULL) " +
            "AND user.id NOT IN (" + USERS_WITH_COMPETITION_INVITE + ") " +
            "AND roles.name = 'assessor' "+
            "GROUP BY user.id ";


    @Query(ASSESSORS_WITH_COMPETITION)
    Page<User> findAssessorsByCompetition(@Param("competitionId") long competitionId, Pageable pageable);

    @Query(ASSESSORS_WITH_COMPETITION)
    List<User> findAssessorsByCompetition(@Param("competitionId") long competitionId);

    @Query(ASSESSORS_WITH_COMPETITION_AND_INNOVATION_AREA)
    Page<User> findAssessorsByCompetitionAndInnovationArea(@Param("competitionId") long competitionId,
                                                           @Param("innovationArea") Long innovationArea,
                                                           Pageable pageable);

    @Query(ASSESSORS_WITH_COMPETITION_AND_INNOVATION_AREA)
    List<User> findAssessorsByCompetitionAndInnovationArea(@Param("competitionId") long competitionId,
                                                           @Param("innovationArea") Long innovationArea);
}
