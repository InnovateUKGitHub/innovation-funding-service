package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    static final String FIND_USER_ORG_SORTED_BY_EMAIL =
            "SELECT NEW org.innovateuk.ifs.user.resource.UserOrganisationResource(CONCAT(u.firstName,' ', u.lastName), o.name, o.id, u.email, u.status)" +
                    "FROM User u\n" +
                    "INNER JOIN u.roles ur\n" +
                    "INNER JOIN UserOrganisation uo ON u.id = uo.user_id\n" +
                    "INNER JOIN Organisation o ON o.id = uo.organisation_id\n" +
                    "WHERE ur.id IN (SELECT r.id FROM Role r WHERE r.name IN ('applicant', 'collaborator','partner','finance_contact','project_manager'))\n" +
                    "ORDER BY u.email";

    @Query(FIND_USER_ORG_SORTED_BY_EMAIL)
    List<UserOrganisationResource> findAllExternalUserOrganisations();

    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") final UserStatus status);

    Optional<User> findByIdAndRolesName(Long id, String name);

    @Override
    List<User> findAll();

    List<User> findByRolesName(String name);

    List<User> findByRolesNameOrderByFirstNameAscLastNameAsc(String name);

    List<User> findByRolesNameInOrderByEmailAsc(Set<String> name);

    Page<User> findDistinctByStatusAndRolesNameIn(UserStatus status, Set<String> roleName, Pageable pageable);

    User findOneByUid(String uid);

}
