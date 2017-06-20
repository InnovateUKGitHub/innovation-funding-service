package org.innovateuk.ifs.profile.repository;

import org.innovateuk.ifs.profile.domain.Profile;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProfileRepository extends CrudRepository<Profile, Long> {
}
