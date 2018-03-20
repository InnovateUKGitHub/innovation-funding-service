package org.innovateuk.ifs.userorganisation.repository;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisationPK;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface UserOrganisationRepository extends PagingAndSortingRepository<UserOrganisation, UserOrganisationPK> {
    List<UserOrganisation> findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByIdUserEmailAsc(String firstName, String lastName, Set<Role> name);
    List<UserOrganisation> findByUserEmailLikeAndUserRolesInOrderByIdUserEmailAsc(String email, Set<Role> name);
    List<UserOrganisation> findByOrganisationNameLikeAndUserRolesInOrderByIdUserEmailAsc(String organisationName, Set<Role> name);
}
