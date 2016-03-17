package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
        Optional<User> findByEmail(@Param("email") String email);
        @Override
        List<User> findAll();
        User findOneByUid(@Param("uid") String uid);
}
