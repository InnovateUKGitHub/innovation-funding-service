package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
        List<User> findByName(@Param("name") String name);
        List<User> findByEmail(@Param("email") String email);
        List<User> findByToken(@Param("token") String token);
        List<User> findAll();
}
