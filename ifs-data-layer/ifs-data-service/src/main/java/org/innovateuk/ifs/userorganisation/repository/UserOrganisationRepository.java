package org.innovateuk.ifs.userorganisation.repository;

import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisationPK;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface UserOrganisationRepository extends PagingAndSortingRepository<UserOrganisation, UserOrganisationPK> {
    List<UserOrganisation> findByUserRolesNameInOrderByIdUserEmailAsc(Set<String> name);
    List<UserOrganisation> findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesNameInOrderByIdUserEmailAsc(String firstName, String lastName, Set<String> name);
    List<UserOrganisation> findByUserEmailLikeAndUserRolesNameInOrderByIdUserEmailAsc(String email, Set<String> name);
    List<UserOrganisation> findByOrganisationNameLikeAndUserRolesNameInOrderByIdUserEmailAsc(String organisationName, Set<String> name);
}
