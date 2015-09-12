package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * This interface generates the REST urls.
 */
@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
        List<User> findByName(@Param("name") String name);
        List<User> findByEmail(@Param("email") String email);
        List<User> findById(@Param("id") Long id);
        List<User> findByToken(@Param("token") String token);
        List<User> findAll();
}
