package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationFinanceRowRepository extends FinanceRowRepository<ApplicationFinanceRow>, PagingAndSortingRepository<ApplicationFinanceRow, Long> {

    List<ApplicationFinanceRow> findByTargetApplicationIdAndTargetOrganisationId(long projectId, long organisationId);
}
