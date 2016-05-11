package com.worth.ifs.alert.repository;

import com.worth.ifs.alert.domain.Alert;
import com.worth.ifs.alert.domain.AlertType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AlertRepository extends CrudRepository<Alert, Long> {

    @Override
    List<Alert> findAll();

    @Query("SELECT a FROM Alert a WHERE CURRENT_TIMESTAMP >= a.validFromDate AND CURRENT_TIMESTAMP <= a.validToDate")
    List<Alert> findAllVisible();

    @Query("SELECT a FROM Alert a WHERE a.type = :type AND CURRENT_TIMESTAMP >= a.validFromDate AND CURRENT_TIMESTAMP <= a.validToDate")
    List<Alert> findAllVisibleByType(@Param("type") final AlertType type);

    void deleteByType(@Param("type") AlertType type);

}
