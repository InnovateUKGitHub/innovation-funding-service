package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
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

    Optional<User> findByIdAndRolesName(Long id, String type);

    @Override
    List<User> findAll();

    List<User> findByRoles_Name(@Param("name") String name);

    User findOneByUid(@Param("uid") String uid);

    @Query(value = "SELECT * " +
            "FROM user u " +
            "   LEFT JOIN user_role ur " +
            "       ON u.id = ur.user_id " +
            "   LEFT JOIN role r " +
            "       ON ur.role_id = r.id " +
            "WHERE r.name = 'assessor' AND u.id NOT IN(" +
            "   SELECT u.id " +
            "   FROM user u " +
            "       LEFT JOIN user_role ur " +
            "           ON u.id = ur.user_id, " +
            "       competition_user cu " +
            "       LEFT JOIN invite i " +
            "           ON cu.invite_id = i.id, " +
            "       role r " +
            "       LEFT JOIN user_role usr_r " +
            "           ON r.id = usr_r.role_id, " +
            "       participant_status ps " +
            "       LEFT JOIN competition_user comp_u " +
            "           ON ps.id = comp_u.participant_status_id " +
            "    WHERE cu.competition_id = :competitionId AND u.email = i.email " +
            "       AND (ps.name = 'PENDING' OR ps.name = 'ACCEPTED') AND r.name = 'assessor')", nativeQuery = true)
    List<User> findAllAvailableAssessorsByCompetition(@Param("competitionId") long competitionId);
}
