package com.worth.ifs.repository;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Organisation;
import com.worth.ifs.domain.User;
import com.worth.ifs.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "organisation", path = "organisation")
public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {
    Organisation findById(@Param("id") Long id);
    Organisation findByUserApplicationRoles(@Param("userApplicationRoles") UserApplicationRole userApplicationRole);
}
