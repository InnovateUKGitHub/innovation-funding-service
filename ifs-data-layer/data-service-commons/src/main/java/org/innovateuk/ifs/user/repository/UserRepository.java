package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Optional<User> findByEmailAndRolesNot(String email, Role role);

    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") final UserStatus status);

    Optional<User> findByIdAndRoles(Long id, Role role);

    @Override
    List<User> findAll();

    List<User> findByRoles(Role role);

    List<User> findByRolesOrderByFirstNameAscLastNameAsc(Role role);

    Page<User> findDistinctByStatusAndRolesIn(UserStatus status, Set<Role> roles, Pageable pageable);

    User findOneByUid(String uid);

}
