package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    List<User> findByRoles_Name(@Param("name") String name);

    User findOneByUid(@Param("uid") String uid);

    String AVAILABLE_ASSESSORS_QUERY =
            "FROM user " +
            "   INNER JOIN user_role ON user.id = user_role.user_id " +
            "   INNER JOIN role ON user_role.role_id = role.id " +
            "   WHERE role.name = 'assessor' " +
            "   AND user.id NOT IN (" +
            "       SELECT user.id " +
            "       FROM user " +
            "           INNER JOIN competition_user ON competition_user.user_id = user.id " +
            "           INNER JOIN invite ON invite.id = competition_user.invite_id " +
            "           INNER JOIN participant_status ON competition_user.participant_status_id = participant_status.id " +
            "       WHERE competition_user.competition_id = :competitionId " +
            "           AND user.email = invite.email)";

    @Query(value = "SELECT * " + AVAILABLE_ASSESSORS_QUERY + " LIMIT :pageStart,:pageEnd", nativeQuery = true)
    List<User> findAllAvailableAssessorsByCompetitionByPage(@Param("competitionId") long competitionId,
                                                            @Param("pageStart") int pageStart,
                                                            @Param("pageEnd") int pageEnd);

    @Query(value = "SELECT COUNT(*) " + AVAILABLE_ASSESSORS_QUERY, nativeQuery = true)
    int countAllAvailableAssessorsByCompetition(@Param("competitionId") long competitionId);
}
