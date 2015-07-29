package com.worth.ifs.repository;

import com.worth.ifs.domain.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by wouter on 29/07/15.
 */
@RepositoryRestResource(collectionResourceRel = "userApplicationRole", path = "userapplicationrole")
public interface UserApplicationRoleRepository extends PagingAndSortingRepository<Role, Long> {
    List<Role> findByName(@Param("name") String name);
    List<Role> findById(@Param("id") Long id);
    List<Role> findAll();
}
