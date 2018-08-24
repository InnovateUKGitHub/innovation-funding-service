package org.innovateuk.ifs.userorganisation.repository;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;

public interface UserOrganisationRepository extends PagingAndSortingRepository<UserOrganisation, Long> {
    Set<UserOrganisation> findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(String firstName, String lastName, Set<Role> name);
    Set<UserOrganisation> finByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(String email, Set<Role> name);
    Set<UserOrganisation> findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(String organisationName, Set<Role> name);
}
