package com.worth.ifs.repository;

import com.worth.ifs.domain.User;
import com.worth.ifs.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "userApplicationRole", path = "userapplicationrole")
public interface UserApplicationRoleRepository extends PagingAndSortingRepository<UserApplicationRole, Long> {
    List<UserApplicationRole> findById(@Param("id") Long id);
    List<UserApplicationRole> findByUser(@Param("user") User user);
}
