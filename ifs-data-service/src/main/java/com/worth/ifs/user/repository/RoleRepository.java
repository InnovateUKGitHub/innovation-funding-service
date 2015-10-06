package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
    List<Role> findByName(@Param("name") String name);
}
