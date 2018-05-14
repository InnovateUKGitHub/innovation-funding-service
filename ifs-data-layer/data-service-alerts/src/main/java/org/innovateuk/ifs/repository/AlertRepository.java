package org.innovateuk.ifs.repository;

import org.innovateuk.ifs.domain.Alert;
import org.innovateuk.ifs.resource.AlertType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AlertRepository extends CrudRepository<Alert, Long> {

    @Override
    List<Alert> findAll();

    @Query("SELECT a FROM Alert a WHERE :time >= a.validFromDate AND :time <= a.validToDate")
    List<Alert> findAllVisible(@Param("time") ZonedDateTime now);

    @Query("SELECT a FROM Alert a WHERE a.type = :type AND :time >= a.validFromDate AND :time <= a.validToDate")
    List<Alert> findAllVisibleByType(@Param("type") AlertType type, @Param("time") ZonedDateTime now);

    void deleteByType(@Param("type") AlertType type);

}
