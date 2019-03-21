package org.innovateuk.ifs.profile.repository;

import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.ZonedDateTime;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AffiliationRepository extends Repository<Affiliation, Long> {

    @Query("SELECT u FROM User u" +
            " WHERE EXISTS " +
                "(SELECT a.id FROM Affiliation a " +
                " WHERE " +
                " a.user = u " +
                " AND a.modifiedOn < :expiry) " +
            " AND u.profileId IS NOT NULL AND EXISTS " +
                " (SELECT p.id FROM Profile p " +
                " WHERE " +
                " p.id = u.profileId " +
                " AND (p.doiNotifiedOn < :expiry OR p.doiNotifiedOn IS NULL))")
    Page<User> findUserToBeNotifiedOfExpiry(ZonedDateTime expiry, Pageable pageable);
}
