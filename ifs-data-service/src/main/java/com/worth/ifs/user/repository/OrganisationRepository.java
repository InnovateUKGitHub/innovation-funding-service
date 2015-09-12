package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "organisation", path = "organisation")
public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {
    Organisation findById(@Param("id") Long id);
    Organisation findByUserApplicationRoles(@Param("userApplicationRoles") UserApplicationRole userApplicationRole);
}
