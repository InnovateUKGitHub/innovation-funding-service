package com.worth.ifs.user.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "userApplicationRole", path = "userapplicationrole")
public interface UserApplicationRoleRepository extends PagingAndSortingRepository<UserApplicationRole, Long> {
    List<UserApplicationRole> findById(@Param("id") Long id);
    List<UserApplicationRole> findByUser(@Param("user") User user);
    List<UserApplicationRole> findByUserAndApplication(@Param("user") User user, @Param("application") Application application);
    List<UserApplicationRole> findByApplication(@Param("application") Application application);
    List<UserApplicationRole> findByApplicationId(@Param("applicationId") Long applicationId);
    UserApplicationRole findByUserIdAndApplicationId(@Param("userId") Long userId, @Param("applicationId") Long applicationId);
}
