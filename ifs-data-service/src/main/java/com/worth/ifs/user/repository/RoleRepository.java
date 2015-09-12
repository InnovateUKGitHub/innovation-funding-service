package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "role", path = "role")
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
    List<Role> findByName(@Param("name") String name);
    List<Role> findById(@Param("id") Long id);
    List<Role> findAll();
}
