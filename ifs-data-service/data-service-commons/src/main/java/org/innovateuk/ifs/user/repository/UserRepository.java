package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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

    Page<User> findByRolesName(String roleName, Pageable pageable);

    Page<User> findByRolesNameAndIdNotIn(String roleName, Collection<Long> userIds, Pageable pageable);

    @Query("SELECT user " +
            "FROM User user " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            "JOIN profile.innovationAreas innovationAreas " +
            "JOIN user.roles roles " +
            "WHERE innovationAreas.category.id = :innovationArea " +
            "AND user.id NOT IN :userIds " +
            "AND roles.name = :roleName")
    Page<User> findByRolesNameAndIdNotInAndProfileInnovationArea(@Param("roleName") String roleName,
                                                                 @Param("userIds") Collection<Long> userIds,
                                                                 @Param("innovationArea") long innovationArea,
                                                                 Pageable pageable);


}
