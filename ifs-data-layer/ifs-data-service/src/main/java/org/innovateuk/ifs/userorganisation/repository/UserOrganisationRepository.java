package org.innovateuk.ifs.userorganisation.repository;

import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisationPK;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface UserOrganisationRepository extends PagingAndSortingRepository<UserOrganisation, UserOrganisationPK> {
    List<UserOrganisation> findByUserRolesNameInOrderByIdUserEmailAsc(Set<String> name);
}
