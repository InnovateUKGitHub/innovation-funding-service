package org.innovateuk.ifs.project.financecheck.repository;

import org.innovateuk.ifs.project.financecheck.domain.EligibilityProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface EligibilityProcessRepository extends ProcessRepository<EligibilityProcess>, PagingAndSortingRepository<EligibilityProcess, Long> {

}
