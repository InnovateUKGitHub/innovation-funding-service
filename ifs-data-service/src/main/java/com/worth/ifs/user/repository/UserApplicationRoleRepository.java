package com.worth.ifs.user.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserApplicationRoleRepository extends PagingAndSortingRepository<UserApplicationRole, Long> {
    List<UserApplicationRole> findById(@Param("id") Long id);
    List<UserApplicationRole> findByUser(@Param("user") User user);
    List<UserApplicationRole> findByUserId(@Param("userId") Long userId);
    List<UserApplicationRole> findByUserAndApplication(@Param("user") User user, @Param("application") Application application);
    List<UserApplicationRole> findByApplication(@Param("application") Application application);
    List<UserApplicationRole> findByApplicationId(@Param("applicationId") Long applicationId);
    UserApplicationRole findByUserIdAndApplicationId(@Param("userId") Long userId, @Param("applicationId") Long applicationId);
}
